<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.area.edit")} - Powered By JQB SHOP</title>
<meta name="author" content="JQB SHOP Team" />
<meta name="copyright" content="JQB SHOP" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
    var $browserButton = $("#browserButton");
	
	[@flash_message /]
    $browserButton.unbind().browser({
    });
	// 表单验证
	$inputForm.validate({
		rules: {
			name: "required",
			order: "digits"
		}
	});
	
});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; ${message("admin.area.edit")}
	</div>
	<form id="inputForm" action="update.jhtml" method="post">
		<input type="hidden" name="id" value="${area.id}" />
		<table class="input">
			<tr>
				<th>
					${message("admin.area.parent")}:
				</th>
				<td>
					[#if area.parent??]${area.parent.name}[#else]${message("admin.area.root")}[/#if]
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Area.name")}:
				</th>
				<td>
					<input type="text" name="name" class="text" value="${area.name}" maxlength="100" />
				</td>
			</tr>
			<tr>
				<th>
					${message("admin.common.order")}:
				</th>
				<td>
					<input type="text" name="order" class="text" value="${area.order}" maxlength="9" />
				</td>
			</tr>
            <tr id="pathTr">
                <th>
                    <span class="requiredField">*</span>${message("Ad.path")}:
                </th>
                <td>
					<span class="fieldSet">
						<input type="text" id="path" name="path" value="${area.path}" class="text" maxlength="200"  />
						<input type="button" id="browserButton" class="button" value="${message("admin.browser.select")}" />
					</span>
                </td>
            </tr>
            <tr>
                <th>
				${message("admin.common.setting")}:
                </th>
                <td>
                    <label>
                        <input type="checkbox" name="isMarketable" value="true" [#if area.isMarketable] checked="checked"[/#if] />${message("Product.isMarketable")}
                        <input type="hidden" name="_isMarketable" value="false" />
                    </label>
                </td>
            </tr>
			<tr>
				<th>
					&nbsp;
				</th>
				<td>
					<input type="submit" class="button" value="${message("admin.common.submit")}" />
					<input type="button" class="button" value="${message("admin.common.back")}" onclick="location.href='list.jhtml[#if area.parent??]?parentId=${area.parent.id}[/#if]'" />
				</td>
			</tr>
		</table>
	</form>
</body>
</html>
