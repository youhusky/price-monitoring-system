# Develop an Amazon Price Monitoring System
Design a price monitoring system to monitor product price and notify subscribers if the price is reduced. Similar to Dealmoon.com.

## Features
- Product Crawler inserts products into different RabbitMQ queues via product categories.
- Price Monitor reads products from the queue, updates product price in cache and DB. It also adds the product to price reduction queue if price is lower than before.
- User subscribes for one or more categories with email. users' information are stored in MySQL.
- Instant notification runs every 10 minutes to read from price reduction queue and notify subscribers.
- User can query price reduction information through APIs as well. (Optional)
- Daily notification runs every day to read from DB and notify subscribers. (Optional)

## Development environment
- **MySQL DB** is used to store categories, product list urls and users information.
- **Lombok** is used to eliminate constructors and getter/setter implementation for cleaner coding style.
- **RabbitMQ** is used for communications between Product Crawler and Price Monitoring Service, also between Price Monitoring Service and Instant Notification Service.
- **Redis** or **Memcached** is used to cache last product price for fast price change comparison.

## Design Diagrams
![Category Crawling Flow](/CategoryCrawlingFlow.png)
![Product Crawling Flow](/ProductCrawlingFlow.png)
![Price Monitoring](/PriceMonitoringFlow.png)
![Instant Notification Flow](/InstantNotificationFlow.png)
![Daily Notification Flow](/DailyNotificationFlow.png)
![User Subscription Flow](/UserSubscriptionFlow.png)

## Category Crawler Design Detail
- Since product category won't change too fast, this crawler is scheduled to run once a week.
- From url: 
```
https://www.amazon.com/s/ref=nb_sb_noss_2?url=search-alias=aps&field-keywords=-12345
```
- Selector:
```
#searchDropdownBox > option:nth-child(n)

Where n >= 2
```
- Get all “search-alias” as category name
- Repeat till empty element returned.
- The product list url for each category is in the following format:
```
https://www.amazon.com/s/ref=nb_sb_noss?url=$SEARCH_ALIAS&field-keywords=-12345&page=$PAGE_NO

Replace the $SEARCH_ALIAS with category name and $PAGE_NO with the desired product list.
```
- Store the <Category name, product list url> pairs into Category DB.

## Product Crawler Design Detail
- This crawler is scheduled to run every 4 hours to quickly catch price changing information.
- If a category contains a lot of products, multiple crawlers can be started to share the load.
- Only subscribed categories will be crawled.
- Title: get title from selector: 
```
#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div:nth-child(1) > a

Where $RESULT_NO starts from 0
```
- Price: get aria-label from selector: 
```
#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(2) > div.a-column.a-span7 > div.a-row.a-spacing-none > a > span
```
- Thumnail: get src from selector: 
```
#result_$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-left > div > div > a > img
```
- Detail_url: get href from selector: 
```
#result _$RESULT_NO > div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div:nth-child(1) > a
```
- product_id: get last portion of detail_url.
