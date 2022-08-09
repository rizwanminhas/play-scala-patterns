package services.impl

import services.GreetingService

class SecondGreetingServiceImpl extends GreetingService {
  override def greet(): String = "Greetings from second implementation!"
}
