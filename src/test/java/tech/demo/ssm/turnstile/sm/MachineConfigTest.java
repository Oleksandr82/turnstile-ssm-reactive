package tech.demo.ssm.turnstile.sm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.test.StateMachineTestPlan;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class MachineConfigTest {

    @Autowired
    StateMachine<DomainState, DomainEvent> machine;

    @BeforeEach
    void setUp() {
        machine.stopReactively().block();
    }

    @Test
    void givenOpen_whenStarted_shouldBeLocked() throws Exception {
        StateMachineTestPlan<DomainState, DomainEvent> plan =
                StateMachineTestPlanBuilder.<DomainState, DomainEvent>builder()
                        .stateMachine(machine)
                        .step()
                        .expectStateMachineStarted(1)
                        .expectState(DomainState.LOCKED)
                        .and().build();
        plan.test();
    }

    @Test
    void givenOpen_whenPush_shouldClose() throws Exception {
        StateMachineTestPlan<DomainState, DomainEvent> plan =
                StateMachineTestPlanBuilder.<DomainState, DomainEvent>builder()
                        .stateMachine(machine)
                        .step()
                        .expectStateMachineStarted(1)
                        .expectState(DomainState.LOCKED)
                        .and().step()
                        .sendEvent(DomainEvent.COIN)
                        .expectStateChanged(1)
                        .expectState(DomainState.UNLOCKED)
                        .and().step()
                        .sendEvent(DomainEvent.PUSH)
                        .expectStateChanged(1)
                        .expectState(DomainState.LOCKED)
                        .and().build();

        plan.test();
    }

}