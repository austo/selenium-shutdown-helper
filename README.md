####Hub:####
```
$ ./run_hub.sh -p src/main/resources/com/moraustin/NodeShutdownProxy.properties -v 2.45.0
```

The options passed abover are the defaults; this is equivalent to running `./run_hub.sh`

####Node:####
```
$ ./run_node.sh -c config/nodeConfig.json -u http://localhost:4444/grid/register -v 2.45.0 
```

The options passed above are the defaults; this is equivalent to running `./run_node.sh`


####Help####
Run `./run_hub.sh -h` or `run_node.sh -h` to see available options and their defaults.