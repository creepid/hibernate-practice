CREATE TABLE `stock` (
  `STOCK_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `STOCK_CODE` varchar(10) NOT NULL,
  `STOCK_NAME` varchar(20) NOT NULL,
  PRIMARY KEY (`STOCK_ID`) USING BTREE,
  UNIQUE KEY `UNI_STOCK_NAME` (`STOCK_NAME`),
  UNIQUE KEY `UNI_STOCK_ID` (`STOCK_CODE`) USING BTREE
)

-- B-tree он же binary tree индекс, это индекс сгруппированный по листьям 
-- бинарного дерева. Применяется для больших индексов, по сути это индекс 
-- индексов. Ну скажем индексы с величиной от 1 до 10 хранятся в одной ветке, 
-- от 11 до 20 в другой и т.д., когда приходит запрос на индекс с номером 35, 
-- идем к 3-й ветке и находим там 5-й элемент.

-- Hash индекс применяется для сравнения/построения индексов строчных и/или 
-- двоичных данных. Каждому значению индексируемого выражения сопоставляется 
-- значение определенной хэш функции отображающей исходное значение 
-- на целое число (иногда на строку).

-- B-Tree индекс дает скорость выборки порядка log(N), hash дает линейную. 
-- В реальной жизни hash и B-Tree применяются совместно, то есть для 
-- вычисления значений B-Tree индекса все равно применяются хэши.
CREATE TABLE `category` (
  `CATEGORY_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `NAME` varchar(10) NOT NULL,
  `DESC` varchar(255) NOT NULL,
  PRIMARY KEY (`CATEGORY_ID`) USING BTREE
)

CREATE TABLE  `stock_category` (
  `STOCK_ID` int(10) unsigned NOT NULL,
  `CATEGORY_ID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`STOCK_ID`,`CATEGORY_ID`),
  CONSTRAINT `FK_CATEGORY_ID` FOREIGN KEY (`CATEGORY_ID`) REFERENCES `category` (`CATEGORY_ID`),
  CONSTRAINT `FK_STOCK_ID` FOREIGN KEY (`STOCK_ID`) REFERENCES `stock` (`STOCK_ID`)
)