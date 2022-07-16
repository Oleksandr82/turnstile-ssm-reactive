package tech.demo.ssm.turnstile.sm;

import java.util.EnumSet;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import tech.demo.ssm.turnstile.sm.actions.BeMoreGenerousAction;
import tech.demo.ssm.turnstile.sm.actions.GoThroughAction;
import tech.demo.ssm.turnstile.sm.actions.MakePaymentAction;
import tech.demo.ssm.turnstile.sm.actions.YouShallNotPassAction;

@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
public class MachineConfig extends EnumStateMachineConfigurerAdapter<DomainState, DomainEvent> {

    private final MachineListener listener;
    private final BeMoreGenerousAction beMoreGenerousAction;
    private final GoThroughAction goThroughAction;
    private final MakePaymentAction makePaymentAction;
    private final YouShallNotPassAction youShallNotPassAction;
    private final StateMachineRuntimePersister<DomainState, DomainEvent, String> persister;

    @Override
    public void configure(StateMachineConfigurationConfigurer<DomainState, DomainEvent> config) throws Exception {
        config.withConfiguration()
                .autoStartup(true)
                .listener(listener);
        config.withPersistence().runtimePersister(persister);
    }

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
                .source(DomainState.LOCKED).target(DomainState.UNLOCKED).action(makePaymentAction)
                .event(DomainEvent.COIN)
                .and()
                .withExternal()
                .source(DomainState.UNLOCKED).target(DomainState.UNLOCKED).action(beMoreGenerousAction)
                .event(DomainEvent.COIN)
                .and()
                .withExternal()
                .source(DomainState.UNLOCKED).target(DomainState.LOCKED).action(goThroughAction)
                .event(DomainEvent.PUSH)
                .and()
                .withExternal()
                .source(DomainState.LOCKED).target(DomainState.LOCKED).action(youShallNotPassAction)
                .event(DomainEvent.PUSH);


    }
}
