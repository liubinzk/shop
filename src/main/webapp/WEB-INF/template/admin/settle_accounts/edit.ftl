<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>编辑商户- Powered By JQB SHOP</title>
<meta name="author" content="JQB SHOP Team" />
<meta name="copyright" content="JQB SHOP" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.autocomplete.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $name = $("#name");
	var $url = $("#url");
	var $code = $("#code");
	
	[@flash_message /]
	

	
	// 表单验证
	$inputForm.validate({
		rules: {
			name: "required",
			code: "required"
		}
	});
	
});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo;编辑商户
	</div>
	<form id="inputForm" action="update.jhtml" method="post">
		<input type="hidden" name="id" value="${settleAccounts.id}" />
		<table class="input">
			<tr>
				<th>
					编号:
				</th>
				<td>
					${settleAccounts.sn}
				</td>
			</tr>
			<tr>
				<th>
					方式:
				</th>
				<td>
					${message("Payment.Method." + settleAccounts.method)}
				</td>
			</tr>
			<tr>
				<th>
					体现金额:
				</th>
				<td>
					${currency(settleAccounts.amount, true)}
				</td>
			</tr>
			<tr>
				<th>
					提现人:
				</th>
				<td>
					${(settleAccounts.admin.username)!"-"}
				</td>
			</tr>
			<tr>
				<th>
					商户:
				</th>
				<td>
								[#if settleAccounts.commercial.id != 0]
									${settleAccounts.commercial.name}
								[/#if]
				</td>
			</tr>	
			<tr>
				<th>
					状态:
				</th>
				<td>
					${message("Payment.Status." + settleAccounts.status)}
				</td>
			</tr>
			<tr>
				<th>
					创建日期:
				</th>
				<td>
					${settleAccounts.createDate?string("yyyy-MM-dd HH:mm:ss")}
				</td>
			</tr>
			<tr>
				<th>
					付款日期:
				</th>
				<td>
								[#if settleAccounts.paymentDate??]
									<span title="${settleAccounts.paymentDate?string("yyyy-MM-dd HH:mm:ss")}">${settleAccounts.paymentDate}</span>
								[#else]
									-
								[/#if]				</td>
			</tr>									
		</table>
	</form>
</body>
</html>
