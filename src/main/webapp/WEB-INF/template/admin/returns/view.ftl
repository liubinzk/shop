<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.returns.view")} - Powered By JQB SHOP</title>
<meta name="author" content="JQB SHOP Team" />
<meta name="copyright" content="JQB SHOP" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {
    var $confirmForm = $("#confirmForm");
    var $denyForm = $("#denyForm");
    var $confirmButton = $("#confirmButton");
    var $denyButton = $("#denyButton");
	[@flash_message /]

    // 确认
    $confirmButton.click(function() {
        var $this = $(this);
        $.dialog({
            type: "warn",
            content: "${message("admin.returns.confirmDialog")}",
            onOk: function() {
                $confirmForm.submit();
            }
        });
    });

    // 拒绝退/换货
    $denyButton.click(function() {
        var $this = $(this);
        $.dialog({
            type: "warn",
            content: "${message("admin.returns.denyDialog")}",
            onOk: function() {
                $denyForm.submit();
            }
        });
    });
	
});
</script>
</head>
<body>
	<form id="confirmForm" action="confirm.jhtml" method="post">
		<input type="hidden" name="id" value="${returns.id}" />
	</form>
	<form id="denyForm" action="deny.jhtml" method="post">
		<input type="hidden" name="id" value="${returns.id}" />
	</form>
	<ul id="tab" class="tab">
		<li>
			<input type="button" value="${message("admin.returns.base")}" />
		</li>
		<li>
			<input type="button" value="${message("admin.returns.returnsItem")}" />
		</li>
	</ul>
	<table class="input tabContent">
        <tr>
            <td>
                &nbsp;
            </td>
            <td>
                <input type="button" id="confirmButton" class="button" value="${message("admin.order.confirm")}"[#if returns.returnsStatus != "unconfirmed"] disabled="disabled"[/#if] />
                <input type="button" id="denyButton" class="button" value="${message("admin.returns.deny")}"[#if returns.returnsStatus != "unconfirmed"] disabled="disabled"[/#if] />
            </td>
            <td>
                &nbsp;
            </td>
            <td>
				[#--[#if returns.serviceType==2 && returns.returnsStatus == "confirmed"]
					<a href="${base}/admin/returns/exchange_order.jhtml?returnsId=${returns.id}&productSn=${returnsItem.sn}&quantity=${returnsItem.quantity}"><input type="button" id="exchangeOrderButton" class="button" value="${message("ReturnsItem.exchangere.order")}" /></a>
				[/#if]--]
            </td>
		</tr>
		<tr>
			<th>
				${message("Returns.sn")}:
			</th>
			<td>
				${returns.sn}
			</td>
			<th>
				${message("admin.common.createDate")}:
			</th>
			<td>
				${returns.createDate?string("yyyy-MM-dd HH:mm:ss")}
			</td>
		</tr>
		<tr>
			<th>
				${message("Returns.shippingMethod")}:
			</th>
			<td>
				${returns.shippingMethod}
			</td>
			<th>
				${message("Returns.deliveryCorp")}:
			</th>
			<td>
				${returns.deliveryCorp}
			</td>
		</tr>
		<tr>
			<th>
				${message("Returns.trackingNo")}:
			</th>
			<td>
				${(returns.trackingNo)!"-"}
			</td>
			<th>
				${message("Returns.freight")}:
			</th>
			<td>
				${currency(returns.freight, true)!"-"}
			</td>
		</tr>
		<tr>
			<th>
				${message("Returns.shipper")}:
			</th>
			<td>
				${returns.shipper}
			</td>
			<th>
				${message("Returns.phone")}:
			</th>
			<td>
				${returns.phone}
			</td>
		</tr>
		<tr>
			<th>
				${message("Returns.area")}:
			</th>
			<td>
				${returns.area}
			</td>
			<th>
				${message("Returns.address")}:
			</th>
			<td>
				${returns.address}
			</td>
		</tr>
		<tr>
			<th>
				${message("Returns.zipCode")}:
			</th>
			<td>
				${returns.zipCode}
			</td>
			<th>
				${message("Returns.order")}:
			</th>
			<td>
				${returns.order.sn}
			</td>
		</tr>
		<tr>
			
			<th>
				${message("Returns.operator")}:
			</th>
			<td>
				${returns.operator}
			</td>
			<th>
				${message("Returns.memo")}:
			</th>
			<td>
				${returns.memo}
			</td>
		</tr>
        <tr>

            <th>
			${message("Returns.status")}:
            </th>
            <td>
			${returns.returnsStatus}
            </td>
            <th>
                &nbsp;
            </th>
            <td>
                &nbsp;
            </td>
        </tr>
	</table>
	<table class="input tabContent">
		<tr class="title">
			<th>
				${message("ReturnsItem.sn")}
			</th>
			<th>
				${message("ReturnsItem.name")}
			</th>
			<th>
				${message("ReturnsItem.quantity")}
			</th>
			<th>
				<span>${message("admin.common.handle")}</span>
			</th>
		</tr>
		[#list returns.returnsItems as returnsItem]
			<tr>
				<td>
					${returnsItem.sn}
				</td>
				<td>
					<span title="${returnsItem.name}">${abbreviate(returnsItem.name, 50, "...")}</span>
				</td>
				<td>
					${returnsItem.quantity}
				</td>
                <td>
					[#if returns.serviceType==2]
							<a href="${base}/admin/returns/exchange_order.jhtml?returnsId=${returns.id}&productSn=${returnsItem.sn}&quantity=${returnsItem.quantity}"><input type="button" id="exchangeOrderButton" class="button" value="${message("ReturnsItem.exchangere.order")}" /></a>
					[/#if]
				</td>
			</tr>
		[/#list]
	</table>
	<table class="input">
		<tr>
			<th>
				&nbsp;
			</th>
			<td>
				<input type="button" class="button" value="${message("admin.common.back")}" onclick="location.href='list.jhtml'" />
			</td>
		</tr>
	</table>
</body>
</html>
