<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>商户列表 - Powered By JQB SHOP</title>
<meta name="author" content="JQB SHOP Team" />
<meta name="copyright" content="JQB SHOP" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/list.js"></script>
<script type="text/javascript">
$().ready(function() {

	[@flash_message /]

});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 商户结算列表 <span>(${message("admin.page.total", page.total)})</span>
	</div>
	<form id="listForm" action="list.jhtml" method="get">
		<div class="bar">
			<a href="add.jhtml" class="iconButton">
				<span class="addIcon">&nbsp;</span>提现
			</a>
			<div class="buttonWrap">
				<a href="javascript:;" id="deleteButton" class="iconButton disabled">
					<span class="deleteIcon">&nbsp;</span>${message("admin.common.delete")}
				</a>
				<a href="javascript:;" id="refreshButton" class="iconButton">
					<span class="refreshIcon">&nbsp;</span>${message("admin.common.refresh")}
				</a>
				<div class="menuWrap">
					<a href="javascript:;" id="pageSizeSelect" class="button">
						${message("admin.page.pageSize")}<span class="arrow">&nbsp;</span>
					</a>
					<div class="popupMenu">
						<ul id="pageSizeOption">
							<li>
								<a href="javascript:;"[#if page.pageSize == 10] class="current"[/#if] val="10">10</a>
							</li>
							<li>
								<a href="javascript:;"[#if page.pageSize == 20] class="current"[/#if] val="20">20</a>
							</li>
							<li>
								<a href="javascript:;"[#if page.pageSize == 50] class="current"[/#if] val="50">50</a>
							</li>
							<li>
								<a href="javascript:;"[#if page.pageSize == 100] class="current"[/#if] val="100">100</a>
							</li>
						</ul>
					</div>
				</div>
			</div>
			<div class="menuWrap">
				<div class="search">
					<span id="searchPropertySelect" class="arrow">&nbsp;</span>
					<input type="text" id="searchValue" name="searchValue" value="${page.searchValue}" maxlength="200" />
					<button type="submit">&nbsp;</button>
				</div>
				<div class="popupMenu">
					<ul id="searchPropertyOption">
						<li>
							<a href="javascript:;"[#if page.searchProperty == "name"] class="current"[/#if] val="name">商户名称</a>
						</li>
					</ul>
				</div>
			</div>
		</div>
			<table id="listTable" class="list">
					<tr>
						<th class="check">
							<input type="checkbox" id="selectAll" />
						</th>
						<th>
							<a href="javascript:;" class="sort" name="sn">${message("Payment.sn")}</a>
						</th>
						<th>
							<a href="javascript:;" class="sort" name="method">${message("Payment.method")}</a>
						</th>
						<th>
							<a href="javascript:;" class="sort" name="amount">提现金额</a>
						</th>
						<th>
							<a href="javascript:;" class="sort" name="admin">提现人</a>
						</th>
						<th>
							<a href="javascript:;" class="sort" name="commercial">商户</a>
						</th>
						<th>
							<a href="javascript:;" class="sort" name="status">${message("Payment.status")}</a>
						</th>
						<th>
							<a href="javascript:;" class="sort" name="paymentDate">${message("Payment.paymentDate")}</a>
						</th>
						<th>
							<a href="javascript:;" class="sort" name="createDate">${message("admin.common.createDate")}</a>
						</th>
						<th>
							<span>${message("admin.common.handle")}</span>
						</th>
					</tr>
					[#list page.content as settleAccounts]
						<tr>
							<td>
								<input type="checkbox" name="ids" value="${settleAccounts.id}" />
							</td>
							<td>
								${settleAccounts.sn}
							</td>
							<td>
								${message("Payment.Method." + settleAccounts.method)}
							</td>
							<td>
								${currency(settleAccounts.amount, true)}
							</td>
							<td>
								${(settleAccounts.admin.username)!"-"}
							</td>
							<td>
								[#if settleAccounts.commercial.id != 0]
									${settleAccounts.commercial.name}
								[/#if]
							</td>
							<td>
								${message("Payment.Status." + settleAccounts.status)}
							</td>
							<td>
								[#if settleAccounts.paymentDate??]
									<span title="${settleAccounts.paymentDate?string("yyyy-MM-dd HH:mm:ss")}">${settleAccounts.paymentDate}</span>
								[#else]
									-
								[/#if]
							</td>
							<td>
								<span title="${settleAccounts.createDate?string("yyyy-MM-dd HH:mm:ss")}">${settleAccounts.createDate}</span>
							</td>
							<td>
								<a href="edit.jhtml?id=${settleAccounts.id}">查看</a>
								[#if admin.username  == "admin"]
									<a href="audit.jhtml?id=${settleAccounts.id}">审核</a>
								[/#if]
							</td>
						</tr>
					[/#list]
				</table>
				[#if !page.content?has_content]
					<p>${message("admin.main.admin.commercial.noResult")}</p>
				[/#if]
		[@pagination pageNumber = page.pageNumber totalPages = page.totalPages]
			[#include "/admin/include/pagination.ftl"]
		[/@pagination]
	</form>
</body>
</html>
