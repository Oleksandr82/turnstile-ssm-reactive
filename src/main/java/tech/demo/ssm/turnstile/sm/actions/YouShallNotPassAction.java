package tech.demo.ssm.turnstile.sm.actions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import tech.demo.ssm.turnstile.sm.DomainEvent;
import tech.demo.ssm.turnstile.sm.DomainState;

@Slf4j
@Component
public class YouShallNotPassAction implements Action<DomainState, DomainEvent> {

    @Override
    public void execute(StateContext<DomainState, DomainEvent> context) {
        log.info("You shall not pass!");
    }
}
