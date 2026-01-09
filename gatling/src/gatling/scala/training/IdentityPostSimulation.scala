package training

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import java.util.UUID
import scala.concurrent.duration._

class IdentityPostSimulation extends Simulation {

  val users = 3000
  val rampUpDuration = 10.seconds
  val rampUpUsers = (users/rampUpDuration).toInt
  val duration = 2.minute

  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .contentTypeHeader("application/json")

  val identityScenario = scenario("Identity POST load")
    .exec(session => session.set("randomId", UUID.randomUUID().toString))
    .exec(
      http("create identity")
        .post("/api/identity")
        .body(StringBody(session => s"{\"id\": \"${session("randomId").as[String]}\"}"))
        .check(status.is(201))
    )

  setUp(
    identityScenario.inject(
      rampUsersPerSec(rampUpUsers) to users during (rampUpDuration),
      constantUsersPerSec(users) during (duration)
    )).protocols(httpProtocol)
}
