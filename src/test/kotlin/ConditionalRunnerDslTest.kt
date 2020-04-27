import actions.createConditionalRunnerScope
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.assertj.core.api.Assertions
import org.junit.Test

class ConditionalRunnerDslTest : BasePlatformTestCase() {

  @Test
  fun testCreateConditionalRunnerScope() {
    var count = 1
    var executionCount = 1
    createConditionalRunnerScope {
      condition { count < 4 }
      addLambda { count++; executionCount++ }
      addLambda { count++; executionCount++ }
      addLambda { count++; executionCount++ }
      addLambda { count++; executionCount++ }
      addLambda { count++; executionCount++ }
      addLambda { count++; executionCount++ }
      runEachLambdaUntilConditionNotMet()
    }
    Assertions.assertThat(count).isEqualTo(4)
    Assertions.assertThat(executionCount).isEqualTo(4)
  }

}