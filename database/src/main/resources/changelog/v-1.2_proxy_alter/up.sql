

ALTER TABLE system.proxies
ADD COLUMN last_used timestamp DEFAULT current_timestamp,
ADD COLUMN banned_by_avito boolean DEFAULT false,
ADD COLUMN banned_by_cian boolean DEFAULT false;

