## Задача поиска среднего числа в p2p сети
### Local voting protocol

### Сборка и запуск
Собираем этой командой
```shell
./gradlew build
```

Топология между агентами описывается в скрипте `run.sh`.
```shell
java -classpath ./build/libs/mt-average-1.0-SNAPSHOT.jar jade.Boot -agents '
  1st:AverageAgent(1,2st,3st);
  2st:AverageAgent(5,1st,3st);
  3st:AverageAgent(7,2st,1st);
  <имя агента>:AverageAgent(<число агента>,<имена смежных агентов через запятую)
' # не забудь кавычку
```

Запускаем вот так:
```shell
./run.sh
```

