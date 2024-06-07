//jdbc 输入流
{
  "jdbcUrl":"",//jdbcUrl
  "userName":"",//用户名
  "password":"",//密码
  "tableName":"",//表名
  "splitPk":"",//切分字段
  "where":"",//where
  "precise":false,//是否精准切分
   "tableNumber":-1,//切分为多少块
   "fetchSize":32,//每次读取的大小
   "samplePercentage":0.1,//切分时候的百分比 oracle生效
  "splitFactor":-1,//切分因素 为 当没有tableNumber 时生效为 executorSize*splitFactor
  "column":["","",""],//字段列
  "mandatoryEncoding":""//强制编码
}