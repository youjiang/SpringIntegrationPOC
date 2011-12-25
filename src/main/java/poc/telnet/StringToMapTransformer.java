package poc.telnet;

import java.util.HashMap;
import java.util.Map;

import org.springframework.integration.Message;
import org.springframework.integration.context.IntegrationObjectSupport;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.Transformer;

public class StringToMapTransformer extends IntegrationObjectSupport implements
		Transformer {

	public Message<?> transform(Message<?> message) {
		String payload = message.getPayload().toString();
		String[] items = payload.split("[:=;]");
		String operation = items[0];
		String type = items[1];

		Map<String, String> result = new HashMap<String, String>();
		for (int i = 2; i < items.length; i += 2) {
			result.put(items[i], items[i + 1]);
		}

		return MessageBuilder.withPayload(result).copyHeaders(message.getHeaders())
				.setHeader("operation", operation).setHeader("type", type).build();
	}
}
