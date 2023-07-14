
--drop table if exists tbl_end_points;
--drop table if exists tbl_roles;
--drop table if exists tbl_users;

-- Insert user data
INSERT INTO public.tbl_users ( address, auth_type, birthday, created_at, created_by, email, password, phone, roles, thumbnail, updated_at, updated_by, upload_number, user_name) VALUES ( null, null, null, '2023-06-26 00:07:05.110505', 'hieunguyen', 'hieunguyen@gmail.com', '$2a$10$6q7fV1UXjIPT4aG1O5NnyOn5JfRFb4cnfj.P.hgwBmg7lxFwREGNa', null, '[2]', 'http://localhost:8763/api/v1/files/images/users/default_user_thumbnail.jpg', '2023-06-26 00:07:05.110505', 'hieunguyen', '{"1": 5, "2": 3}', 'hieunguyen');
INSERT INTO public.tbl_users ( address, auth_type, birthday, created_at, created_by, email, password, phone, roles, thumbnail, updated_at, updated_by, upload_number, user_name) VALUES ( null, null, null, '2023-06-26 00:07:05.110506', 'huynguyen', 'huynguyen@gmail.com', '$2a$10$6q7fV1UXjIPT4aG1O5NnyOn5JfRFb4cnfj.P.hgwBmg7lxFwREGNa', null, '[2]', 'http://localhost:8763/api/v1/files/images/users/default_user_thumbnail.jpg', '2023-06-26 00:07:05.110505', 'hieunguyen', '{"1": 5, "2": 3}', 'huynguyen');
INSERT INTO public.tbl_users ( address, auth_type, birthday, created_at, created_by, email, password, phone, roles, thumbnail, updated_at, updated_by, upload_number, user_name) VALUES ( null, null, null, '2023-06-26 00:07:05.110506', 'minhnguyen', 'minhnguyen@gmail.com', '$2a$10$6q7fV1UXjIPT4aG1O5NnyOn5JfRFb4cnfj.P.hgwBmg7lxFwREGNa', null, '[2]', 'http://localhost:8763/api/v1/files/images/users/default_user_thumbnail.jpg', '2023-06-26 00:07:05.110505', 'hieunguyen', '{"1": 5, "2": 3}', 'minhnguyen');
INSERT INTO public.tbl_users ( address, auth_type, birthday, created_at, created_by, email, password, phone, roles, thumbnail, updated_at, updated_by, upload_number, user_name) VALUES ( null, null, null, '2023-06-26 00:07:23.000000', 'hieunguyen', 'admin@gmail.com', '$2a$10$6q7fV1UXjIPT4aG1O5NnyOn5JfRFb4cnfj.P.hgwBmg7lxFwREGNa', null, '[3]', 'http://localhost:8763/api/v1/files/images/users/default_user_thumbnail.jpg', '2023-06-26 00:07:05.110505', 'hieunguyen', null, 'admin');
INSERT INTO public.tbl_users ( address, auth_type, birthday, created_at, created_by, email, password, phone, roles, thumbnail, updated_at, updated_by, upload_number, user_name) VALUES ( null, null, null, '2023-06-26 00:07:23.000000', 'hieunguyen', 'admin1@gmail.com', '$2a$10$6q7fV1UXjIPT4aG1O5NnyOn5JfRFb4cnfj.P.hgwBmg7lxFwREGNa', null, '[3]', 'http://localhost:8763/api/v1/files/images/users/default_user_thumbnail.jpg', '2023-06-26 00:07:05.110505', 'hieunguyen', null, 'admin1');
-- Insert role data
INSERT INTO public.tbl_roles ( created_at, created_by, end_point, role_name, updated_at, updated_by,deleted ) VALUES ( '2023-06-25 23:41:43.000000', 'hieunguyen', '[62,70]', 'USER_EXAM', null, 'hieunguyen',false );
INSERT INTO public.tbl_roles ( created_at, created_by, end_point, role_name, updated_at, updated_by,deleted ) VALUES ( '2023-06-25 23:41:10.000000', 'hieunguyen', '[19, 20, 21, 45, 46, 48, 49, 50, 51, 59, 60, 62, 65, 67, 68, 69,70]', 'USER', null, 'hieunguyen',false );
INSERT INTO public.tbl_roles ( created_at, created_by, end_point, role_name, updated_at, updated_by,deleted ) VALUES ( '2023-06-25 23:41:20.000000', 'hieunguyen', '[17, 19, 20, 21, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 59, 60, 69]', 'ADMIN', null, 'hieunguyen',false );
INSERT INTO public.tbl_roles ( created_at, created_by, end_point, role_name, updated_at, updated_by,deleted ) VALUES ( '2023-06-25 23:40:22.000000', 'hieunguyen', '[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 52, 53, 54, 55, 56, 57, 58, 61, 63, 64, 66]', 'PUBLIC', null, 'hieunguyen',false );
INSERT INTO public.tbl_roles ( created_at, created_by, end_point, role_name, updated_at, updated_by,deleted ) VALUES ( '2023-06-25 23:41:10.000000', 'hieunguyen', '[19, 20, 21, 45, 46, 48, 49, 50, 51, 59, 60, 62, 65, 67, 68, 69,70]', 'USER_PREMIUM', null, 'hieunguyen',false );
-- Insert endpoint data
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 10:29:53.000000', 'hieunguyen', '/api/v1/auth/login', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 10:30:20.000000', 'hieunguyen', '/api/v1/auth/register', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 10:30:33.000000', 'hieunguyen', '/api/v1/auth/logout', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 10:31:57.000000', 'hieunguyen', '/eureka', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 10:32:13.000000', 'hieunguyen', '/fallback', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 10:33:14.000000', 'hieunguyen', '/api/v1/auth/refreshToken', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 10:33:42.000000', 'hieunguyen', '/api/v1/auth/accounts-exam', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 10:34:23.000000', 'hieunguyen', '/api/v1/articles', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 10:35:00.000000', 'hieunguyen', '/api/v1/articles/name', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 10:35:18.000000', 'hieunguyen', '/api/v1/articles/id', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 11:22:32.000000', 'hieunguyen', '/api/v1/files/images', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 22:29:32.000000', 'hieunguyen', '/api/v1/exams/categories/get', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 22:30:11.000000', 'hieunguyen', '/api/v1/exams/categories/options', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 22:30:40.000000', 'hieunguyen', '/api/v1/exams/get', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 22:31:21.000000', 'hieunguyen', '/api/v1/exams/durations', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 22:35:08.000000', 'hieunguyen', '/api/v1/files/images/{domain}/{imageName}', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:42:57.000000', 'hieunguyen', '/api/v1/auth/users', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:43:19.000000', 'hieunguyen', '/api/v1/auth/getEndPointsByRoles', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:43:53.000000', 'hieunguyen', '/api/v1/auth/user/info', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:45:14.000000', 'hieunguyen', '/api/v1/auth/update/info', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:45:49.000000', 'hieunguyen', '/api/v1/auth/update/thumbnail', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:47:14.000000', 'hieunguyen', '/api/v1/auth/accounts-exam/registerAccountExam', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:47:58.000000', 'hieunguyen', '/api/v1/auth/roles', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:48:15.000000', 'hieunguyen', '/api/v1/auth/roles/getEndPointsByRole', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:48:29.000000', 'hieunguyen', '/api/v1/auth/roles/detail', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:48:44.000000', 'hieunguyen', '/api/v1/auth/roles/create', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:48:59.000000', 'hieunguyen', '/api/v1/auth/roles/edit', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:49:45.000000', 'hieunguyen', '/api/v1/auth/endpoints', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:50:08.000000', 'hieunguyen', '/api/v1/auth/endpoints/options', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:50:21.000000', 'hieunguyen', '/api/v1/auth/endpoints/create', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:50:33.000000', 'hieunguyen', '/api/v1/auth/endpoints/edit', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:50:53.000000', 'hieunguyen', '/api/v1/auth/endpoints/delete', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:51:46.000000', 'hieunguyen', '/api/v1/articles/create', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:52:11.000000', 'hieunguyen', '/api/v1/articles/update', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:52:18.000000', 'hieunguyen', '/api/v1/articles/update-img', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:52:38.000000', 'hieunguyen', '/api/v1/articles/delete', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:53:28.000000', 'hieunguyen', '/api/v1/files/image/updateImage', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:53:35.000000', 'hieunguyen', '/api/v1/files', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:56:03.000000', 'hieunguyen', '/api/v1/exams/categories/detail', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:56:18.000000', 'hieunguyen', '/api/v1/exams/categories/create', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:56:25.000000', 'hieunguyen', '/api/v1/exams/categories/update/thumbnail', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:56:37.000000', 'hieunguyen', '/api/v1/exams/categories/update/info', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:56:51.000000', 'hieunguyen', '/api/v1/exams/categories/delete', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:58:19.000000', 'hieunguyen', '/api/v1/notify/topics', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:58:51.000000', 'hieunguyen', '/api/v1/notify/topic/subscribe', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:59:22.000000', 'hieunguyen', '/api/v1/notify/topic/unsubscribe', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-25 23:59:54.000000', 'hieunguyen', '/api/v1/notify/topics/delete', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-26 00:01:03.000000', 'hieunguyen', '/api/v1/notify/user', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-27 10:23:06.000000', 'hieunguyen', '/api/v1/exams/create', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-27 17:49:34.000000', 'hieunguyen', '/api/v1/exams/options', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-27 17:56:49.000000', 'hieunguyen', '/api/v1/exams/downloadExam', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-30 14:01:19.000000', 'hieunguyen', '/api/v1/articles/v3/api-docs', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-30 14:11:54.000000', 'hieunguyen', '/api/v1/auth/v3/api-docs', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-30 15:57:11.000000', 'hieunguyen', '/api/v1/files/v3/api-docs', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-30 15:57:30.000000', 'hieunguyen', '/api/v1/exams/v3/api-docs', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-06-30 15:58:56.000000', 'hieunguyen', '/api/v1/notify/v3/api-docs', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-07-03 20:36:41.000000', 'hieunguyen', '/api/v1/exams/hot/category', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-07-04 11:21:31.000000', 'hieunguyen', '/api/v1/exams/orderByOptions', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-07-06 21:08:34.000000', 'hieunguyen', '/api/v1/exams/delete', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-07-07 22:01:31.000000', 'hieunguyen', '/api/v1/exams/detail', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-07-07 22:13:37.000000', 'hieunguyen', '/api/v1/exams/name', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-07-08 01:20:12.000000', 'hieunguyen', '/api/v1/exams/fetch', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-07-08 11:18:08.000000', 'hieunguyen', '/api/v1/exams/random', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-07-09 22:24:17.000000', 'hieunguyen', '/api/v1/exams/feedback/exam', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-07-09 22:24:27.000000', 'hieunguyen', '/api/v1/exams/feedback/check', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-07-09 22:25:27.000000', 'hieunguyen', '/api/v1/exams/feedback/calculate', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-07-09 22:25:47.000000', 'hieunguyen', '/api/v1/exams/feedback/create', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-07-09 22:26:09.000000', 'hieunguyen', '/api/v1/exams/feedback/edit', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-07-09 22:26:53.000000', 'hieunguyen', '/api/v1/exams/feedback/delete', null, null);
INSERT INTO public.tbl_end_points ( created_at, created_by, end_point, updated_at, updated_by) VALUES ( '2023-07-14 17:08:09.000000', 'hieunguyen', '/api/v1/exams/submit', null, null);
