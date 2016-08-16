<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.productCategory.add")} - Powered By JQB SHOP</title>
<meta name="author" content="JQB SHOP Team" />
<meta name="copyright" content="JQB SHOP" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.lSelect.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<style type="text/css">
.brands label {
	width: 150px;
	display: block;
	float: left;
	padding-right: 6px;
}
</style>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");

    var $productCategoryTable = $("#productCategoryTable");
    var $productCategoryImageTable = $("#productCategoryImageTable");
    var $addProductCategoryImage = $("#addProductCategoryImage");
    var $deleteProductCategoryImage = $("a.deleteProductImage");
    var productCategoryImageIndex = 0;

	[@flash_message /]

	// 表单验证
	$inputForm.validate({
		rules: {
			name: "required",
			order: "digits"
		},
        submitHandler: function(form) {
            var isRepeats = false;
            if (!isRepeats) {
                form.submit();
            }
        }
	});

    // 增加商品图片
    $addProductCategoryImage.click(function() {
	[@compress single_line = true]
        var trHtml =
                '<tr>
                <td>
                <input type="file" name="productCategoryImages[' + productCategoryImageIndex + '].file" class="productImageFile" \/>
					<\/td>
				<td>
				<input type="text" name="productCategoryImages[' + productCategoryImageIndex + '].title" class="text" maxlength="200" \/>
					<\/td>
				<td>
				<input type="text" name="productCategoryImages[' + productCategoryImageIndex + '].order" class="text productImageOrder" maxlength="9" style="width: 50px;" \/>
					<\/td>
				<td>
				<a href="javascript:;" class="deleteProductCategoryImage">[${message("admin.common.delete")}]<\/a>
				<\/td>
				<\/tr>';
	[/@compress]
        $productCategoryImageTable.append(trHtml);
        productCategoryImageIndex ++;
    });

    // 删除商品图片
    $deleteProductCategoryImage.live("click", function() {
        var $this = $(this);
        $.dialog({
            type: "warn",
            content: "${message("admin.dialog.deleteConfirm")}",
            onOk: function() {
                $this.closest("tr").remove();
            }
        });
    });
	
});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; ${message("admin.productCategory.add")}
	</div>
	<form id="inputForm" action="save.jhtml" method="post" enctype="multipart/form-data">
        <ul id="tab" class="tab">
            <li>
                <input type="button" value="${message("admin.product.base")}" />
            </li>
            <li>
                <input type="button" value="${message("admin.product.productImage")}" />
            </li>
		</ul>
		<table class="input tabContent"  id="productCategoryTable" >
			<tr>
				<th>
					<span class="requiredField">*</span>${message("ProductCategory.name")}:
				</th>
				<td>
					<input type="text" id="name" name="name" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("ProductCategory.parent")}:
				</th>
				<td>
					<select name="parentId">
						<option value="">${message("admin.productCategory.root")}</option>
						[#list productCategoryTree as category]
							<option value="${category.id}">
								[#if category.grade != 0]
									[#list 1..category.grade as i]
										&nbsp;&nbsp;
									[/#list]
								[/#if]
								${category.name}
							</option>
						[/#list]
					</select>
				</td>
			</tr>
			<tr class="brands">
				<th>
					${message("ProductCategory.brands")}:
				</th>
				<td>
					[#list brands as brand]
						<label>
							<input type="checkbox" name="brandIds" value="${brand.id}" />${brand.name}
						</label>
					[/#list]
				</td>
			</tr>
			<tr>
				<th>
					${message("ProductCategory.seoTitle")}:
				</th>
				<td>
					<input type="text" name="seoTitle" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("ProductCategory.seoKeywords")}:
				</th>
				<td>
					<input type="text" name="seoKeywords" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("ProductCategory.seoDescription")}:
				</th>
				<td>
					<input type="text" name="seoDescription" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("admin.common.order")}:
				</th>
				<td>
					<input type="text" name="order" class="text" maxlength="9" />
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
        <table id="productCategoryImageTable" class="input tabContent">
            <tr>
                <td colspan="4">
                    <a href="javascript:;" id="addProductCategoryImage" class="button">${message("admin.product.addProductImage")}</a>
                </td>
            </tr>
            <tr class="title">
                <td>
				${message("ProductImage.file")}
                </td>
                <td>
				${message("ProductImage.title")}
                </td>
                <td>
				${message("admin.common.order")}
                </td>
                <td>
				${message("admin.common.delete")}
                </td>
            </tr>
        </table>
        <table class="input">
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
