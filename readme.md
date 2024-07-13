
### Run directly

```ps1
java -jar .\target\slicer.jar -c de.uni_passau.fim.se2.sa.examples.Calculator -m "evaluate:(Ljava/lang/String;)I" -v sum -l 6
```

### Run with bash script

```sh
./run.sh -c de.uni_passau.fim.se2.sa.examples.Calculator -m "evaluate:(Ljava/lang/String;)I" -v sum -l 6
```

### Launching agent

```ps1
java -javaagent:.\target\slicer.jar \
    -jar .\target\slicer.jar \
    -c de.uni_passau.fim.se2.sa.examples.Calculator \
    -m "evaluate:(Ljava/lang/String;)I" \
    -v sum -l 6 -d true
```
