hostIp=127.0.0.1
hostId=ubuntu
hostPw=ubuntu
nodeIp=127.0.0.1
nodeId=ubuntu
nodePw=ubuntu
port=13579
version=0.0.1
sshpass -p$hostPw scp -P1022 deviceLogQuery-$version.jar $hostId@$hostIp:~;
sshpass -p$hostPw ssh $hostIp -p1022 "kill -9 \$(ps aux | grep 'java -jar' | grep -v grep | awk '{print \$2}')";
sshpass -p$hostPw ssh -L $port:127.0.0.1:$port $hostId@$hostIp -p1022 "java -jar deviceLogQuery-$version.jar --server.port=$port --host.user=ubuntu --host.pw=$hostPw --node.user=$nodeId --node.pw=$nodePw --node.ip=$nodeIp"
	 
