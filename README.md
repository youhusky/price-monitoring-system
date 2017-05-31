# Develop an Amazon Price Monitoring System

## Design Diagrams
![Category Crawling Flow](/CategoryCrawlingFlow.png)
![Product Crawling Flow](/ProductCrawlingFlow.png)
![Price Monitoring](/PriceMonitoringFlow.png)
![Instant Notification Flow](/InstantNotificationFlow.png)
![Daily Notification Flow](/DailyNotificationFlow.png)
![User Subscription Flow](/UserSubscriptionFlow.png)

## Category Crawler Design Detail
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
https://www.amazon.com/s/ref=nb_sb_noss?url=search-alias=$SEARCH_ALIAS&field-keywords=-12345&page=$PAGE_NO

Replace the $SEARCH_ALIAS with category name and $PAGE_NO with the desired product list.
```
- Store the <Category name, product list url> pairs into Category DB.

## Product Crawler Design Detail
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
