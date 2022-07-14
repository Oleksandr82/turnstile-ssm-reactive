package tech.demo.ssm.turnstile.sm;

import java.util.EnumSet;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Configuration
@EnableStateMachine
public class MachineConfig extends EnumStateMachineConfigurerAdapter<DomainState, DomainEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<DomainState, DomainEvent> states) throws Exception {
        states.withStates()
                .initial(DomainState.LOCKED)
                .states(EnumSet.allOf(DomainState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<DomainState, DomainEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(DomainState.LOCKED).target(DomainState.UNLOCKED)
                .event(DomainEvent.COIN)
                .and()
                .withExternal()
                .source(DomainState.UNLOCKED).target(DomainState.LOCKED)
                .event(DomainEvent.PUSH);
    }
}
