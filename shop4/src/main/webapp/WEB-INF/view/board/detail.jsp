<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/jspHeader.jsp" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시물 상세보기</title>
<style type="text/css">
	.leftcol {text-align: left; vertical-align: top;}
	.lefttoptable{
		height: 250px;	border-width: 0px;
		text-align: left;	vertical-align: top;	padding: 0px;
	}
</style>
</head>
<body>
<table><tr><td colspan="2">Spring 게시판</td></tr>
	<tr><td width="15%">글쓴이</td>
		<td width="85%" class="leftcol">${board.name}</td></tr>
	<tr><td>제목</td><td class="leftcol">${board.subject}</td></tr>
	<tr><td>내용</td><td class="leftcol">
		<table class="lefttoptable">
		<tr><td class="leftcol lefttoptable">${board.content}</td></tr>
		</table></td></tr>
	<tr><td>첨부파일</td><td>&nbsp;
		<c:if test="${!empty board.fileurl}">
		<a href="file/${board.fileurl}">${board.fileurl}</a></c:if></td></tr>
	<tr><td colspan="2">
		<a href="reply.shop?num=${board.num}">[답변]</a>
		<a href="update.shop?num=${board.num}">[수정]</a>
		<a href="delete.shop?num=${board.num}">[삭제]</a>
		<a href="list.shop">[게시물목록]</a>
	</td></tr>
</table>
</body>
</html>