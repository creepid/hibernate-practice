DROP TABLE IF EXISTS `stock`;
CREATE TABLE `stock` (
  `STOCK_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `STOCK_CODE` varchar(10) NOT NULL,
  `STOCK_NAME` varchar(20) NOT NULL,
  PRIMARY KEY (`STOCK_ID`) USING BTREE,
  UNIQUE KEY `UNI_STOCK_NAME` (`STOCK_NAME`),
  UNIQUE KEY `UNI_STOCK_ID` (`STOCK_CODE`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `mkyongdb`.`stock_daily_record`;
CREATE TABLE  `mkyongdb`.`stock_daily_record` (
  `RECORD_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `PRICE_OPEN` float(6,2) DEFAULT NULL,
  `PRICE_CLOSE` float(6,2) DEFAULT NULL,
  `PRICE_CHANGE` float(6,2) DEFAULT NULL,
  `VOLUME` bigint(20) unsigned DEFAULT NULL,
  `DATE` date NOT NULL,
  `STOCK_ID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`RECORD_ID`) USING BTREE,
  UNIQUE KEY `UNI_STOCK_DAILY_DATE` (`DATE`),
  KEY `FK_STOCK_TRANSACTION_STOCK_ID` (`STOCK_ID`),
  CONSTRAINT `FK_STOCK_TRANSACTION_STOCK_ID` FOREIGN KEY (`STOCK_ID`) 
-- CASCADE – означает распространение действий родительской таблицы на дочерние: 
-- то есть, если значение ключа в родительской таблице изменится, то оно автоматически 
-- (!без специальных запросов) изменится и в дочерних таблицах.
-- Изменим значение ключа в таблице пользователей с правилом ON UPDATE CASCADE 
-- и убедимся, что таблица заказов сама изменила значение соответствующего поля userid.
-- ON DELETE CASCADE - эта опция означает каскадное удаление: то есть если 
-- будет удалена строка из родительсткой таблицы, то автоматически будут 
-- удалены строки с соответсвующим внешним ключом из дочерней таблицы. 
-- Для нашего случая это означает, что мы исключаем ситуацию покупки товара, 
-- которого нет в базе данных: если удалится товар, то и удалятся записи о 
-- заказе соответвующего товара. Например, удалим запись из таблицы товаров 
-- с ключом prodid=3. Поскольку внешний ключ prodid в таблице заказов использует
-- правило ON DELETE CASCADE, это значит, что автоматически с записью из таблицы 
-- products, удалятся записи в таблице orders, в которых поле prodid=3.

DELETE FROM products WHERE prodid=3;
UPDATE users SET userid=55 WHERE userid=1 LIMIT 1;
  REFERENCES `stock` (`STOCK_ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8;