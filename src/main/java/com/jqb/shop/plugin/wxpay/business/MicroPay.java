package com.jqb.shop.plugin.wxpay.business;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;

import com.jqb.shop.plugin.wxpay.api.WxPayApi;
import com.jqb.shop.plugin.wxpay.api.WxPayData;
import com.jqb.shop.plugin.wxpay.lib.WxPayException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;


public class MicroPay {

    private static Logger Log = Logger.getLogger(MicroPay.class);

    /**
     * ˢ��֧������ҵ�������߼�
     *
     * @param body
     *            ��Ʒ����
     * @param total_fee
     *            �ܽ��
     * @param auth_code
     *            ֧����Ȩ��
     * @throws WxPayException
     * @return ˢ��֧�����
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws NoSuchAlgorithmException
     * @throws InterruptedException
     */
    public static String Run(String body, String total_fee, String auth_code)
            throws WxPayException, NoSuchAlgorithmException,
            ParserConfigurationException, SAXException, IOException,
            InterruptedException {
        Log.info("Micropay is processing...");

        WxPayData data = new WxPayData();
        data.SetValue("auth_code", auth_code);// ��Ȩ��
        data.SetValue("body", body);// ��Ʒ����
        data.SetValue("total_fee", Integer.parseInt(total_fee));// �ܽ��
        data.SetValue("out_trade_no", WxPayApi.GenerateOutTradeNo());// ����������̻�������

        WxPayData result = WxPayApi.payScanOrder(data, 10); // �ύ��ɨ֧�������շ��ؽ��

        // ����ύ��ɨ֧���ӿڵ���ʧ�ܣ������쳣
        if (!result.IsSet("return_code")
                || result.GetValue("return_code").toString()
                .equalsIgnoreCase("FAIL")) {
            String returnMsg = result.IsSet("return_msg") ? result.GetValue(
                    "return_msg").toString() : "";
            Log.error("Micropay API interface call failure, result : "
                    + result.ToXml());
            throw new WxPayException(
                    "Micropay API interface call failure, return_msg : "
                            + returnMsg);
        }

        // ǩ����֤
        result.CheckSign();
        Log.debug("Micropay response check sign success");

        // ˢ��֧��ֱ�ӳɹ�
        if (result.GetValue("return_code").toString()
                .equalsIgnoreCase("SUCCESS")
                && result.GetValue("result_code").toString()
                .equalsIgnoreCase("SUCCESS")) {
            Log.debug("Micropay business success, result : " + result.ToXml());
            return result.ToPrintStr();
        }

        /******************************************************************
         * ʣ�µĶ��ǽӿڵ��óɹ���ҵ��ʧ�ܵ����
         * ****************************************************************/
        // 1��ҵ������ȷʧ��
        if (!result.GetValue("err_code").toString()
                .equalsIgnoreCase("USERPAYING")
                && !result.GetValue("err_code").toString()
                .equalsIgnoreCase("SYSTEMERROR")) {
            Log.error("micropay API interface call success, business failure, result : "
                    + result.ToXml());
            return result.ToPrintStr();
        }

        // 2������ȷ���Ƿ�ʧ�ܣ���鵥
        // ���̻�������ȥ�鵥
        String out_trade_no = data.GetValue("out_trade_no").toString();

        // ȷ��֧���Ƿ�ɹ�,ÿ��һ��ʱ���ѯһ�ζ���������ѯ10��
        int queryTimes = 10;// ��ѯ����������
        while (queryTimes-- > 0) {
            ResultCode succCode = new ResultCode();// ��ѯ���
            WxPayData queryResult = Query(out_trade_no, succCode);
            // �����Ҫ������ѯ����ȴ�2s�����
            if (succCode.succResult == 2) {
                Thread.sleep(2000);
                continue;
            }
            // ��ѯ�ɹ�,���ض�����ѯ�ӿڷ��ص�����
            else if (succCode.succResult == 1) {
                Log.debug("Mircopay success, return order query result : "
                        + queryResult.ToXml());
                return queryResult.ToPrintStr();
            }
            // ��������ʧ�ܣ�ֱ�ӷ���ˢ��֧���ӿڷ��صĽ����ʧ��ԭ�����err_code������
            else {
                Log.error("Micropay failure, return micropay result : "
                        + result.ToXml());
                return result.ToPrintStr();
            }
        }

        // ȷ��ʧ�ܣ���������
        Log.error("Micropay failure, Reverse order is processing...");
        if (!Cancel(out_trade_no, 0)) {
            Log.error("Reverse order failure");
            throw new WxPayException("Reverse order failure��");
        }

        return result.ToPrintStr();
    }

    /**
     *
     * ��ѯ�������
     *
     * @param out_trade_no string
     *             �̻�������
     * @param  succCode int ��ѯ���������0��ʾ�������ɹ���1��ʾ�����ɹ���2��ʾ������ѯ
     * @return ������ѯ�ӿڷ��ص����ݣ��μ�Э��ӿ�
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws WxPayException
     * @throws NoSuchAlgorithmException
     */
    public static WxPayData Query(String out_trade_no, ResultCode succCode)
            throws NoSuchAlgorithmException, WxPayException,
            ParserConfigurationException, SAXException, IOException {
        WxPayData queryOrderInput = new WxPayData();
        queryOrderInput.SetValue("out_trade_no", out_trade_no);
        WxPayData result = WxPayApi.OrderQuery(queryOrderInput, 0);

        if (result.GetValue("return_code").toString()
                .equalsIgnoreCase("SUCCESS")
                && result.GetValue("result_code").toString()
                .equalsIgnoreCase("SUCCESS")) {
            // ֧���ɹ�
            if (result.GetValue("trade_state").toString()
                    .equalsIgnoreCase("SUCCESS")) {
                succCode.succResult = 1;
                return result;
            }
            // �û�֧���У���Ҫ������ѯ
            else if (result.GetValue("trade_state").toString()
                    .equalsIgnoreCase("USERPAYING")) {
                succCode.succResult = 2;
                return result;
            }
        }

        // ������ش�����Ϊ���˽��׶����Ų����ڡ���ֱ���϶�ʧ��
        if (result.GetValue("err_code").toString()
                .equalsIgnoreCase("ORDERNOTEXIST")) {
            succCode.succResult = 0;
        } else {
            // �����ϵͳ�������������
            succCode.succResult = 2;
        }
        return result;
    }

    /**
     *
     * �������������ʧ�ܻ��ظ�����10��
     *
     * @param out_trade_no string
     *             �̻�������
     * @param depth
     *            ���ô����������õݹ���ȱ�ʾ
     * @return false��ʾ����ʧ�ܣ�true��ʾ�����ɹ�
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws WxPayException
     * @throws NoSuchAlgorithmException
     */
    public static boolean Cancel(String out_trade_no, int depth)
            throws NoSuchAlgorithmException, WxPayException,
            ParserConfigurationException, SAXException, IOException {
        if (depth > 10) {
            return false;
        }

        WxPayData reverseInput = new WxPayData();
        reverseInput.SetValue("out_trade_no", out_trade_no);
        WxPayData result = WxPayApi.Reverse(reverseInput, 0);

        // �ӿڵ���ʧ��
        if (!result.GetValue("return_code").toString()
                .equalsIgnoreCase("SUCCESS")) {
            return false;
        }

        // ������Ϊsuccess�Ҳ���Ҫ���µ��ó��������ʾ�����ɹ�
        if (!result.GetValue("result_code").toString()
                .equalsIgnoreCase("SUCCESS")
                && result.GetValue("recall").toString().equalsIgnoreCase("N")) {
            return true;
        } else if (result.GetValue("recall").toString().equalsIgnoreCase("Y")) {
            return Cancel(out_trade_no, ++depth);
        }
        return false;
    }

    static class ResultCode {
        int succResult = 0;
    }
}
