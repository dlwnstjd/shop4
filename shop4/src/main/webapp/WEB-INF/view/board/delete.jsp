<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/jspHeader.jsp" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판 삭제 화면</title>
</head>
<body>
<form:form modelAttribute="board" action="delete.shop"
	method="post" name="f">
	<form:hidden path="num"/>
	<spring:hasBindErrors name="board">
		<font color="red"><c:forEach items="${errors.globalErrors}"
		var="error"><spring:message code="${error.code}"/></c:forEach></font>
	</spring:hasBindErrors>
	<input type="hidden" name="num" value="${board.num}">
	<table><caption>Spring 게시글 삭제 화면</caption>
		<tr><td>게시글 비밀번호</td>
			<td><form:password path="pass"/></td></tr>
		<tr><td colspan="2">
		<a href="javascript:document.f.submit()">[게시글삭제]</a></td></tr>
	</table>
</form:form>
</body>
</html>