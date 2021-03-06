package tech.demo.ssm.turnstile.sm;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.test.StateMachineTestPlan;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class MachineConfigTest {

    @Autowired
    StateMachineFactory<DomainState, DomainEvent> machineFactory;

    private StateMachine<DomainState, DomainEvent> machine;

    @BeforeEach
    void setUp() {
        machine = machineFactory.getStateMachine("test-ssm");
        machine.stopReactively().block();
    }

    @Test
    void givenStarted_shouldBeLocked() throws Exception {
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

    @Test
    void givenOpen_whenMoreCoins_shouldStayOpen() throws Exception {
        StateMachineTestPlan<DomainState, DomainEvent> plan =
                StateMachineTestPlanBuilder.<DomainState, DomainEvent>builder()
                        .stateMachine(machine)
                        .step()
                        .expectStateMachineStarted(1)
                        .expectState(DomainState.LOCKED)
                        .and().step()
                        .sendEvent(DomainEvent.COIN)
                        .sendEvent(DomainEvent.COIN)
                        .sendEvent(DomainEvent.COIN)
                        .expectStateChanged(3)
                        .expectState(DomainState.UNLOCKED)
                        .and().build();

        plan.test();
    }

    @Test
    void givenClosed_whenPushHard_shouldStayClosed() throws Exception {
        StateMachineTestPlan<DomainState, DomainEvent> plan =
                StateMachineTestPlanBuilder.<DomainState, DomainEvent>builder()
                        .stateMachine(machine)
                        .step()
                        .expectStateMachineStarted(1)
                        .expectState(DomainState.LOCKED)
                        .and().step()
                        .sendEvent(DomainEvent.PUSH)
                        .sendEvent(DomainEvent.PUSH)
                        .sendEvent(DomainEvent.PUSH)
                        .expectStateChanged(3)
                        .expectState(DomainState.LOCKED)
                        .and().build();

        plan.test();
    }
}