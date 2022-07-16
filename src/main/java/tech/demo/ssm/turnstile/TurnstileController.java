package tech.demo.ssm.turnstile;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import tech.demo.ssm.turnstile.sm.DomainEvent;
import tech.demo.ssm.turnstile.sm.DomainState;

@Slf4j
@RestController
@RequestMapping("turnstile/user/{userId}")
@RequiredArgsConstructor
public class TurnstileController {

    private final StateMachineFactory<DomainState, DomainEvent> machineFactory;

    private final ConcurrentMap<String, StateMachine<DomainState, DomainEvent>> machines = new ConcurrentHashMap<>();

    private Mono<StateMachine<DomainState, DomainEvent>> getMachine(String id) {
        return Mono.just(id)
                .publishOn(Schedulers.boundedElastic())
                .map(user -> machines.computeIfAbsent(id, machineFactory::getStateMachine));
    }

    @PostMapping("/coin")
    public Flux<DomainState> dropCoin(@PathVariable String userId) {
        return sendEventToStateMachine(userId, DomainEvent.COIN);
    }

    @PostMapping("/push")
    public Flux<DomainState> pushIt(@PathVariable String userId) {
        return sendEventToStateMachine(userId, DomainEvent.PUSH);
    }

    @GetMapping("/state")
    public Mono<DomainState> getState(@PathVariable String userId) {
        return getMachine(userId).map(sm -> sm.getState().getId());
    }

    private Flux<DomainState> sendEventToStateMachine(String userId, DomainEvent coin) {
        Message<DomainEvent> eventMessage = MessageBuilder.withPayload(coin).build();
        return getMachine(userId)
                .flatMapMany(sm -> sm.sendEvent(Mono.just(eventMessage)))
                .map(result -> result.getRegion().getState().getId());
    }
}
