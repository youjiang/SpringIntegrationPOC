/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package poc.telnet;

import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.integration.Message;
import org.springframework.integration.ip.tcp.connection.AbstractTcpConnectionInterceptor;
import org.springframework.integration.message.GenericMessage;
import org.springframework.integration.support.MessageBuilder;

/**
 * @author Gary Russell
 * @since 2.0
 * 
 */
public class AuthenticationInterceptor extends AbstractTcpConnectionInterceptor {

	Log logger = LogFactory.getLog(this.getClass());

	private volatile long timeout = 10000;

	private volatile String userName;

	public AuthenticationInterceptor(Authentication authentication) {
		this.authentication = authentication;
	}

	private Authentication authentication;

	private boolean authenticated;

	@Override
	public boolean onMessage(Message<?> message) {
		String payload = new String((byte[]) message.getPayload());
		if (payload.startsWith("login")) {
			String[] items = payload.split("[:;]");
			if (items.length == 3) {
				String userName = items[1];
				String password = items[2];
				boolean authenticated = this.authentication.authenticate(
						userName, password);
				String msg = null;
				if (authenticated) {
					this.authenticated = authenticated;
					this.userName = userName;
					msg = "logined";
				} else {
					msg = "wrong user name or password";
				}
				try {
					super.send(MessageBuilder.withPayload(msg).build());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} else if (payload.startsWith("logout")) {
			super.close();
		} else {
			if (authenticated) {
				MessageBuilder<String> builder = MessageBuilder
						.withPayload(payload);
				builder.copyHeaders(message.getHeaders()).setHeader("userName",
						this.userName);
				return super.onMessage(builder.build());

			} else {
				try {
					super.send(new GenericMessage<String>(
							"Have not login yet. please login with login:username:password"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		return true;

	}

	// @Override
	// public void send(Message<?> message) throws Exception {
	// this.pendingSend = true;
	// try {
	// if (!this.negotiated) {
	// if (!this.isServer()) {
	// logger.debug(this.toString() + " Sending " + userName);
	// super.send(MessageBuilder.withPayload(userName).build());
	// this.negotiationSemaphore.tryAcquire(this.timeout,
	// TimeUnit.MILLISECONDS);
	// if (!this.negotiated) {
	// throw new MessagingException("Negotiation error");
	// }
	// }
	// }
	// super.send(message);
	// } finally {
	// this.pendingSend = false;
	// this.checkDeferredClose();
	// }
	// }
	//
	// /**
	// * Defer the close until we've actually sent the data after negotiation
	// */
	// @Override
	// public void close() {
	// if (this.negotiated && !this.pendingSend) {
	// super.close();
	// return;
	// }
	// closeReceived = true;
	// logger.debug("Deferring close");
	// }
	//
	// /**
	// * Execute the close, if deferred
	// */
	// private void checkDeferredClose() {
	// if (this.closeReceived) {
	// logger.debug("Executing deferred close");
	// this.close();
	// }
	// }

	public Authentication getAuthentication() {
		return authentication;
	}

}
