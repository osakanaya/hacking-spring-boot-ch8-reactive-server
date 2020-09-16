package om.greglturnquist.hackingspringboot.reactive.server;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

@Controller
public class RSocketService {

		private final ItemRepository repository;
		private final EmitterProcessor<Item> itemProcessor;
		private final FluxSink<Item> itemSink;
		
		public RSocketService(ItemRepository repository) {
			this.repository = repository;
			
			this.itemProcessor = EmitterProcessor.create();
			this.itemSink = this.itemProcessor.sink();
		}
		
		@MessageMapping("newItems.request-response")
		public Mono<Item> processNewItemsViaRSockerRequestResponse(Item item) {
			return this.repository.save(item)
				.doOnNext(savedItem -> this.itemSink.next(savedItem));
		}
		
		@MessageMapping("newItems.fire-and-forget")
		public Mono<Void> processNewItemViaRSocketFireAndForget(Item item) {
			return this.repository.save(item)
				.doOnNext(savedItem -> this.itemSink.next(savedItem))
				.then();
		}
		
		@MessageMapping("newItems.monitor")
		public Flux<Item> monitorNewItems() {
			return this.itemProcessor;
		}
}
