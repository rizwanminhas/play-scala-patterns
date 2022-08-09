package controllers

import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.GreetingService

import javax.inject.{Inject, Named}

class DIController @Inject() (
                               cc: ControllerComponents,
                               @Named("first") first: GreetingService,
                               @Named("second") second: GreetingService)
  extends AbstractController(cc) {

  def test(implementation: String): Action[AnyContent] = Action {
    implementation match {
      case "first" => Ok(first.greet())
      case "second" => Ok(second.greet())
      case _ => Ok("unknown implementation!")
    }
  }
}
