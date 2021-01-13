dir=lib
libs=""
for file in $dir/*; do
    libs=$libs:$file
done

libs=ClientTest-1.0.jar:$libs
java -classpath $libs ClientTest.WpaiClient 10.10.1.62 50050 67 /nas/geoserver/Data/02_Cliped/GF2_PMS1_E81_8_N43_6_20170829_snyDov_LS_DN /home/guoshanxin/code/ClientTest/target/result