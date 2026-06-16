INSERT INTO carrier (carrier_code, carrier_name, tracking_url_pattern) VALUES
    ('DHL', 'Dalsey, Hillblom and Lynn', 'https://www.dhl.com/us-en/home/tracking.html?tracking-id=%s&submit=1'),
    ('FEDEX', 'Federal Express', 'https://www.fedex.com/fedextrack/?action=track&trackingnumber=%s'),
    ('UPS', 'United Parcel Service', 'http://wwwapps.ups.com/WebTracking/processRequest?HTMLVersion=5.0&Requester=NES&AgreeToTermsAndConditions=yes&loc=en_US&tracknum=%s'),
    ('USPS', 'United States Postal Service', 'https://tools.usps.com/go/TrackConfirmAction.action?tLabels=%s');

INSERT INTO `condition` (condition_id, condition_code, condition_description, condition_text) VALUES
    (1, 'M', 'Mint', NULL),
    (2, 'E', 'Excellent', NULL),
    (3, 'VG', 'Very Good', NULL),
    (4, 'G', 'Good', NULL),
    (5, 'P', 'Poor', NULL),
    (6, 'NA', 'Not Applicable', NULL),
    (7, 'F', 'Fair', NULL),
    (8, 'MS', 'Missing', NULL),
    (9, 'CC', 'Color Copy', NULL),
    (10, 'BW', 'Black & White Copy', NULL),
    (11, 'SL', 'Sealed', 'Mint in sealed box');

INSERT INTO cost_type (cost_type_code, cost_type_name, cost_type_description) VALUES
    ('DISCOUNT', 'Discount', 'A Discount added to an item or entire order'),
    ('FEE', 'Fee', 'A fee added to an item or entire order'),
    ('INSURANCE', 'Insurance', 'The cost for insurance to ship an item or an entire order'),
    ('PRICE', 'Price', 'The for-sale price or purchase cost of an item'),
    ('SHIPPING', 'Shipping', 'The cost of shipping an item or an entire order'),
    ('TAX', 'Tax', 'The sales tax amount added to an item or entire order');

INSERT INTO item_inventory_state (inventory_state_code, inventory_state_name, inventory_state_description, active, sort_order) VALUES
    ('AVAILABLE', 'Available', 'Owned item is available for normal inventory workflows and may be listed when sale intent allows it.', TRUE, 10),
    ('RESERVED_FOR_ORDER', 'Reserved for Order', 'Owned item is temporarily reserved for an external marketplace order and should not be listed again.', TRUE, 20),
    ('SOLD', 'Sold', 'Owned item has been sold and should not be listed.', TRUE, 30),
    ('REMOVED', 'Removed', 'Owned item has been intentionally removed from the collection outside a marketplace sale.', TRUE, 40),
    ('LOST', 'Lost', 'Owned item cannot currently be located and should not be listed.', TRUE, 50);

INSERT INTO item_inventory_sale_intent (sale_intent_code, sale_intent_name, sale_intent_description, active, sort_order) VALUES
    ('SELLABLE', 'Sellable', 'May be listed for sale when inventory state is AVAILABLE.', TRUE, 10),
    ('KEEP', 'Keep', 'Personal collection item that should not be listed for sale.', TRUE, 20),
    ('UNDECIDED', 'Undecided', 'Sale intent has not been classified; item should not be listed automatically.', TRUE, 30);

INSERT INTO transaction_type (transaction_type_code, transaction_type_description, conversion_factor) VALUES
    ('A', 'Auction Purchase', 1),
    ('B', 'Built from Loose Pieces', 1),
    ('F', 'Find', 1),
    ('G', 'Gift', 1),
    ('P', 'Purchase', 1),
    ('RM', 'Removed', -1),
    ('S', 'Sale', -1),
    ('TG', 'Trade Give', -1),
    ('TR', 'Trade Receive', 1),
    ('YS', 'Yard Sale/Flea Market Purchase', 1);

INSERT INTO transaction_platform (transaction_platform_id, transaction_platform_name) VALUES
    (1, 'Bricklink'),
    (2, 'eBay'),
    (3, 'Private'),
    (4, 'CataWiki'),
    (5, 'Lauritz');

INSERT INTO payment_platform (payment_platform_id, payment_platform_name, payment_platform_url) VALUES
    (1, 'PayPal', 'https://www.paypal.com/'),
    (2, 'Credit Card', NULL);

INSERT INTO external_service_type (external_service_type_id, external_service_type_name, external_service_type_description) VALUES
    (1, 'LEGO', 'LEGO'),
    (2, 'MARKETPLACE', 'Marketplace'),
    (3, 'AUCTION', 'Auction'),
    (4, 'THIRDPARTY', 'Third Party Producer'),
    (5, 'IMAGE_HOSTING', 'Image Hosting Service');

INSERT INTO external_service (external_service_id, service_code, display_name, service_url, external_service_type_id) VALUES
    (1, 'LEGO', 'LEGO', 'https://www.lego.com', 1),
    (2, 'BRICKLINK', 'BrickLink', 'https://www.bricklink.com', 2),
    (3, 'EBAY', 'eBay', 'https://www.ebay.com', 3),
    (4, 'CATAWIKI', 'CataWiki', 'https://www.catawiki.com', 3),
    (5, 'LAURITZ', 'Lauritz', 'https://www.lauritz.com', 3),
    (6, 'QXL', 'QXL', 'https://www.qxl.dk', 3),
    (7, 'PNW_STEAM_SHOP', 'PNW Steam Shop', 'https://www.pnwsteamshop.com', 4),
    (8, 'BRICK_MODEL_RAILROADER', 'Brick Model Railroader', 'https://brickmodelrailroader.com', 4),
    (9, 'REBRICKABLE', 'Rebrickable', 'https://rebrickable.com', 2),
    (10, 'FLICKR', 'Flickr', 'https://www.flickr.com', 5);

INSERT INTO external_service_capability (external_service_id, capability_code) VALUES
    (2, 'CATALOG'),
    (2, 'MARKETPLACE_LISTING'),
    (2, 'ORDER_SYNC'),
    (2, 'PRICE_GUIDE'),
    (3, 'MARKETPLACE_LISTING'),
    (3, 'MARKET_RESEARCH'),
    (9, 'CATALOG'),
    (10, 'IMAGE_HOSTING');

INSERT INTO external_category (external_category_id, external_service_id, external_category_key, category_name, parent_external_category_id) VALUES
    (1, 2, '5', 'Brick', NULL),
    (2, 2, '65', 'Star Wars', NULL),
    (3, 2, '789', 'The Hobbit and The Lord of the Rings', NULL),
    (4, 9, '1', 'Technic', NULL),
    (5, 9, '18', 'Star Wars', 4);
