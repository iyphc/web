INSERT INTO car_brands (brand_id, brand_name, manufacturer_name) VALUES (1, 'Toyota', 'Toyota Motor Corporation');
INSERT INTO car_brands (brand_id, brand_name, manufacturer_name) VALUES (2, 'BMW', 'Bayerische Motoren Werke AG');
INSERT INTO car_brands (brand_id, brand_name, manufacturer_name) VALUES (3, 'LADA', 'AvtoVAZ');
INSERT INTO car_brands (brand_id, brand_name, manufacturer_name) VALUES (4, 'Hyundai', 'Hyundai Motor Company');
INSERT INTO car_brands (brand_id, brand_name, manufacturer_name) VALUES (5, 'Audi', 'Audi AG');

INSERT INTO clients (client_id, full_name, address, phone, email) VALUES (1, 'Ivanov Ivan Ivanovich', 'Moscow, Lesnaya st., 10', '+79990000001', 'ivanov@example.com');
INSERT INTO clients (client_id, full_name, address, phone, email) VALUES (2, 'Petrov Petr Sergeevich', 'Saint Petersburg, Nevsky ave., 20', '+79990000002', 'petrov@example.com');
INSERT INTO clients (client_id, full_name, address, phone, email) VALUES (3, 'Sidorova Anna Viktorovna', 'Kazan, Baumana st., 15', '+79990000003', 'sidorova@example.com');
INSERT INTO clients (client_id, full_name, address, phone, email) VALUES (4, 'Kuznetsov Dmitry Olegovich', 'Ekaterinburg, Mira st., 8', '+79990000004', 'kuznetsov@example.com');
INSERT INTO clients (client_id, full_name, address, phone, email) VALUES (5, 'Smirnova Elena Pavlovna', 'Novosibirsk, Krasny ave., 44', '+79990000005', 'smirnova@example.com');

INSERT INTO cars (car_id, brand_id, registration_number, engine_volume_l, engine_power_hp, fuel_consumption_l_100km, doors_count, seats_count, trunk_capacity_l, transmission_type, has_cruise_control, required_fuel, has_air_conditioner, has_radio, has_video_system, has_gps, interior_trim, color, mileage_km, last_service_date, price)
VALUES (1, 1, 'A123BC77', 2.0, 150, 7.4, 4, 5, 500, 'AT', TRUE, 'AI-95', TRUE, TRUE, FALSE, TRUE, 'Fabric', 'White', 25000, '2026-02-10', 3200000.00);
INSERT INTO cars (car_id, brand_id, registration_number, engine_volume_l, engine_power_hp, fuel_consumption_l_100km, doors_count, seats_count, trunk_capacity_l, transmission_type, has_cruise_control, required_fuel, has_air_conditioner, has_radio, has_video_system, has_gps, interior_trim, color, mileage_km, last_service_date, price)
VALUES (2, 2, 'B456CD77', 3.0, 245, 9.8, 4, 5, 480, 'AMT', TRUE, 'AI-98', TRUE, TRUE, TRUE, TRUE, 'Leather', 'Black', 12000, '2026-01-25', 6100000.00);
INSERT INTO cars (car_id, brand_id, registration_number, engine_volume_l, engine_power_hp, fuel_consumption_l_100km, doors_count, seats_count, trunk_capacity_l, transmission_type, has_cruise_control, required_fuel, has_air_conditioner, has_radio, has_video_system, has_gps, interior_trim, color, mileage_km, last_service_date, price)
VALUES (3, 3, 'C789EF16', 1.6, 106, 6.9, 4, 5, 470, 'MT', FALSE, 'AI-92', TRUE, TRUE, FALSE, FALSE, 'Fabric', 'Blue', 42000, '2026-02-05', 1450000.00);
INSERT INTO cars (car_id, brand_id, registration_number, engine_volume_l, engine_power_hp, fuel_consumption_l_100km, doors_count, seats_count, trunk_capacity_l, transmission_type, has_cruise_control, required_fuel, has_air_conditioner, has_radio, has_video_system, has_gps, interior_trim, color, mileage_km, last_service_date, price)
VALUES (4, 4, 'D321GH78', 2.0, 180, 8.1, 5, 5, 540, 'CVT', TRUE, 'AI-95', TRUE, TRUE, FALSE, TRUE, 'Combined', 'Gray', 8000, '2026-02-18', 4100000.00);
INSERT INTO cars (car_id, brand_id, registration_number, engine_volume_l, engine_power_hp, fuel_consumption_l_100km, doors_count, seats_count, trunk_capacity_l, transmission_type, has_cruise_control, required_fuel, has_air_conditioner, has_radio, has_video_system, has_gps, interior_trim, color, mileage_km, last_service_date, price)
VALUES (5, 5, 'E654IJ77', 2.0, 190, 7.6, 4, 5, 460, 'AT', TRUE, 'DT', TRUE, TRUE, TRUE, TRUE, 'Alcantara', 'Red', 5000, '2026-02-20', 5300000.00);

INSERT INTO orders (order_id, ordered_at, client_id, car_id, need_test_drive, status)
VALUES (1, '2026-02-20 11:00:00', 1, 1, TRUE, 'IN_PROGRESS');
INSERT INTO orders (order_id, ordered_at, client_id, car_id, need_test_drive, status)
VALUES (2, '2026-02-21 14:30:00', 2, NULL, FALSE, 'WAITING_SUPPLY');
INSERT INTO orders (order_id, ordered_at, client_id, car_id, need_test_drive, status)
VALUES (3, '2026-02-22 16:45:00', 3, 4, FALSE, 'IN_SHOWROOM');
INSERT INTO orders (order_id, ordered_at, client_id, car_id, need_test_drive, status)
VALUES (4, '2026-02-23 10:15:00', 4, 2, TRUE, 'TEST_DRIVE');
INSERT INTO orders (order_id, ordered_at, client_id, car_id, need_test_drive, status)
VALUES (5, '2026-02-24 09:20:00', 5, 5, FALSE, 'COMPLETED');

INSERT INTO order_requirements (order_id, desired_brand_id, desired_engine_volume_min, desired_engine_power_min, desired_transmission_type, desired_required_fuel, desired_color, desired_price_max, comment_text)
VALUES (1, 1, 1.8, 140, 'AT', 'AI-95', 'White', 3500000.00, 'Family sedan with automatic transmission');
INSERT INTO order_requirements (order_id, desired_brand_id, desired_engine_volume_min, desired_engine_power_min, desired_transmission_type, desired_required_fuel, desired_color, desired_price_max, comment_text)
VALUES (2, 2, 2.5, 220, 'AMT', 'AI-98', 'Black', 7000000.00, 'Premium vehicle ordered from supplier');
INSERT INTO order_requirements (order_id, desired_brand_id, desired_engine_volume_min, desired_engine_power_min, desired_transmission_type, desired_required_fuel, desired_color, desired_price_max, comment_text)
VALUES (3, 4, 2.0, 170, 'CVT', 'AI-95', 'Gray', 4300000.00, 'Vehicle should be available in showroom');
INSERT INTO order_requirements (order_id, desired_brand_id, desired_engine_volume_min, desired_engine_power_min, desired_transmission_type, desired_required_fuel, desired_color, desired_price_max, comment_text)
VALUES (4, 3, 1.6, 100, 'MT', 'AI-92', 'Blue', 1700000.00, 'Budget vehicle with test drive');
INSERT INTO order_requirements (order_id, desired_brand_id, desired_engine_volume_min, desired_engine_power_min, desired_transmission_type, desired_required_fuel, desired_color, desired_price_max, comment_text)
VALUES (5, 5, 2.0, 180, 'AT', 'DT', 'Red', 5600000.00, 'Client approved the selected vehicle');

INSERT INTO test_drives (test_drive_id, client_id, car_id, test_drive_at, notes)
VALUES (1, 1, 1, '2026-02-20 12:00:00', 'Test drive completed successfully');
INSERT INTO test_drives (test_drive_id, client_id, car_id, test_drive_at, notes)
VALUES (2, 2, 2, '2026-02-21 15:00:00', 'Client liked acceleration');
INSERT INTO test_drives (test_drive_id, client_id, car_id, test_drive_at, notes)
VALUES (3, 3, 4, '2026-02-22 17:10:00', 'Comfort and seating position checked');
INSERT INTO test_drives (test_drive_id, client_id, car_id, test_drive_at, notes)
VALUES (4, 4, 2, '2026-02-23 11:00:00', 'Transmission modes reviewed');
INSERT INTO test_drives (test_drive_id, client_id, car_id, test_drive_at, notes)
VALUES (5, 5, 5, '2026-02-24 10:00:00', 'Client confirmed purchase after drive');
