package modules

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import services.GreetingService
import services.impl.{FirstGreetingServiceImpl, SecondGreetingServiceImpl}

class MyAppModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[GreetingService])
      .annotatedWith(Names.named("first"))
      .to(classOf[FirstGreetingServiceImpl])
      .asEagerSingleton()

    bind(classOf[GreetingService])
      .annotatedWith(Names.named("second"))
      .to(classOf[SecondGreetingServiceImpl])
      .asEagerSingleton()
  }
}
