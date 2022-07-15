package tech.demo.ssm.turnstile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.demo.ssm.turnstile.sm.DomainEvent;
import tech.demo.ssm.turnstile.sm.DomainState;

@Slf4j
@RestController
@RequestMapping("turnstile")
@RequiredArgsConstructor
public class TurnstileController {

    private final StateMachine<DomainState, DomainEvent> machine;

    @PostMapping("/coin")
    public Flux<DomainState> dropCoin() {
        return machine.sendEvent(Mono.just(MessageBuilder.withPayload(DomainEvent.COIN).build()))
                .map(result -> result.getRegion().getState().getId());
    }

    @PostMapping("/push")
    public Flux<DomainState> pushIt() {
        return machine.sendEvent(Mono.just(MessageBuilder.withPayload(DomainEvent.PUSH).build()))
                .map(result -> result.getRegion().getState().getId());
    }

    @GetMapping("/state")
    public Mono<DomainState> getState() {
        return Mono.justOrEmpty(machine.getState().getId());
    }
}
