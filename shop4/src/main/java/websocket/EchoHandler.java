package websocket;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class EchoHandler extends TextWebSocketHandler
							implements InitializingBean{
	//연결된 클라이언트 목록
	private Set<WebSocketSession> clients =
							new HashSet<WebSocketSession>();
	@Override	//연결되는 경우
	public void afterConnectionEstablished(WebSocketSession session) 
												throws Exception {
		super.afterConnectionEstablished(session);
		System.out.println("클라이언트 접속: " + session.getId());
		clients.add(session);		
	}
	@Override	//메세지 수신시
	public void handleMessage(WebSocketSession session,
					WebSocketMessage<?> message) throws Exception {
		//클라이언트가 전송한 메세지 내용
		String loadMessage = (String)message.getPayload();
		System.out.println
			(session.getId() + ":클라이언트 메세지: " + loadMessage);
		clients.add(session);
		for(WebSocketSession s : clients) {
			//접속된 모든 클라이언트에 수신된 메세지 전송
			s.sendMessage(new TextMessage(loadMessage));
		}
	}
	@Override	//오류 발생시
	public void handleTransportError(WebSocketSession session,
			Throwable exception) throws Exception {
		super.handleTransportError(session, exception);
		System.out.println("오류 발생: " + exception.getMessage());
	}
	@Override	//연결 종료시
	public void afterConnectionClosed(WebSocketSession session,
			CloseStatus status) throws Exception {
		super.afterConnectionClosed(session, status);
		System.out.println("클라이언트 접속 해제: " + status.getReason());
		clients.remove(session);
	}
	@Override
	public void afterPropertiesSet() throws Exception {
	}
}
