<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>添加商户 - Powered By JQB SHOP</title>
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
			code: "required",
			account: { required:true,digits:true},
			depositBank: "required"
		}
	});
	
});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 添加商户
	</div>
	<form id="inputForm" action="save.jhtml" method="post">
		<table class="input">
			<tr>
				<th>
					<span class="requiredField">*</span>名称:
				</th>
				<td>
					<input type="text" id="name" name="name" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					网址:
				</th>
				<td>
					<input type="text" id="url" name="url" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>工商号:
				</th>
				<td>
					<input type="text" id="code" name="code" class="text" maxlength="200"  />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>银行账号:
				</th>
				<td>
					<input type="text" name="account" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>开户行:
				</th>
				<td>
					<input type="text" name="depositBank" class="text" maxlength="200" />
				</td>
			</tr>			
			<tr>
				<th>
					描述:
				</th>
				<td>
					<input type="text" name="introduction" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					&nbsp;
				</th>
				<td>
					<input type="submit" class="button" value="${message("admin.common.submit")}" />
					<input type="button" class="button" value="${message("admin.common.back")}" onclick="location.href='list.jhtml'" />
				</td>
			</tr>
		</table>
	</form>
</body>
</html>
