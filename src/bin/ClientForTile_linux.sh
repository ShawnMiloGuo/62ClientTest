dir=lib
libs=""
for file in $dir/*; do
    libs=$libs:$file
done

libs=ClientTest-1.0.jar:$libs
java -classpath $libs ClientTest.WpaiClient 10.10.1.62 50050 4 /home/guoshanxin/Work/TestTiles/ /home/guoshanxin/code/ClientTest/target/result false