<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/jspHeader.jsp" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>MaPage</title>
<script type="text/javascript">
	$(document).ready(function(){
		$("#minfo").show();	//회원정보는 보이도록
		$("#oinfo").hide();	//주문내역은 안보이도록
		$(".saleLine").each(function(){
			$(this).hide();
		})
		$("#tab1").addClass("select");
	})
	function disp_div(id,tab){
		$(".info").each(function(){
			$(this).hide();
		})
		$(".tab").each(function(){
			$(this).removeClass("select");
		})
		$("#"+id).show();
		$("#"+tab).addClass("select");
	}
	function list_disp(id){
		$("#"+id).toggle();	//
	}
</script>
<style type="text/css">
	.select{
		padding:3px;
		background-color: #0000ff;
	}
	.select>a{
		color:#ffffff;
		text-decoration: none;
		font-weight: bold;
	}
</style>
</head>
<body>
<table>
	<tr><td id="tab1" class="tab">
		<a href="javascript:disp_div('minfo','tab1')">회원정보보기</a></td>
		<c:if test="${param.id != 'admin'}">
			<td id="tab2" class="tab">
		<a href="javascript:disp_div('oinfo','tab2')">주문정보보기</a></td>
		</c:if></tr>
</table>
<%-- oinfo: 주문 정보 출력 --%>
<div id="oinfo" class="info" style="display:none; width:100%;">
	<table>
		<tr><th>주문번호</th><th>주문일자</th><th>총주문금액</th></tr>
		<c:forEach items="${salelist}" var="sale" varStatus="stat">
			<tr><td align="center">
			<a href="javascript:list_disp('saleLine${stat.index}')">
			${sale.saleid}</a></td>
			<td align="center"><fmt:formatDate value="${sale.saledate}"
									pattern="yyyy-MM-dd"/>
			</td><td align="right"><fmt:formatNumber value="${sale.total}" pattern="#,###"/>원</td></tr>
			<tr id="saleLine${stat.index}" class="saleLine">
			<td colspan="3" align="center">
			<table>
				<tr><th width="25%">상품명</th><th width="25%">상품가격</th>
				<th width="25%">구매수량</th><th width="25%">상품총액</th>
				<c:forEach items="${sale.itemList}" var="saleItem">
					<tr><td class="title">
						${saleItem.item.name}</td>
						<td><fmt:formatNumber value="${saleItem.item.price}" pattern="#,###"/>원</td>
						<td>${saleItem.quantity}개</td>
					<td><fmt:formatNumber value="${saleItem.quantity * saleItem.item.price}" pattern="#,###"/>원</td></tr>
				</c:forEach>
			</table></td></tr>
		</c:forEach>
	</table>
</div>
<%-- minfo: 회원정보출력 --%>
<div id="minfo" class="info">
	<table>
		<tr><td>아이디</td><td>${user.userid}</td></tr>
		<tr><td>이름</td><td>${user.username}</td></tr>
		<tr><td>우편번호</td><td>${user.postcode}</td></tr>
		<tr><td>전화번호</td><td>${user.phoneno}</td></tr>
		<tr><td>주소</td><td>${user.address}</td></tr>
		<tr><td>이메일</td><td>${user.email}</td></tr>
		<tr><td>생년월일</td><td><fmt:formatDate
			value="${user.birthday}" pattern="yyyy-MM-dd"/></td></tr>
	</table><br>
	<a href="update.shop?id=${user.userid}">[회원정보수정]</a>&nbsp;
	<c:if test="${loginUser.userid != 'admin'}">
		<a href="delete.shop?id=${user.userid}">[회원탈퇴]</a>&nbsp;
	</c:if>
	<c:if test="${loginUser.userid == 'admin'}">
		<a href="../admin/list.shop">[회원목록]</a>&nbsp;
	</c:if>
</div><br>
</body>
</html>