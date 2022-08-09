package services.impl

import services.GreetingService

class FirstGreetingServiceImpl extends GreetingService {
  override def greet(): String = "Greetings from first implementation!"
}
