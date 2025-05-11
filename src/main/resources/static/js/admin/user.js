(function () {
    var app = angular.module('app');

    app.controller('UserController', function($scope, $rootScope, $http, $routeParams, PaginationService, $timeout) {
        $scope.action = $routeParams.action;
        $scope.items = [];
        // $scope.authorities = [];
        $scope.form = { enabled: true, authorities: [] };
        $scope.sortColumn = "username";
        $scope.reverse = false;// false = tăng dần
        var host = "http://localhost:8080/admin/users";
        console.log($scope.action);

        $scope.delete = function (key) {
            var url = `${host}/${key}`;
            $http.delete(url).then(response => {
                var index = $scope.items.findIndex(item => item.username === key);
                $scope.items.splice(index, 1);
                $scope.reset();
                console.log(response.data);
                Swal.fire({
                    icon: 'success',
                    title: 'Xóa thành công!',
                    showConfirmButton: false,
                    timer: 1000
                });
            }).catch(error => {
                console.log(error);
                Swal.fire({
                    icon: 'error',
                    title: 'Xóa thất bại!',
                    text: 'Đã có lỗi xảy ra. Vui lòng thử lại.'
                });
            })
        }
        $scope.edit = function (key) {
            var url = `${host}/${key}`;
            $http.get(url).then(response => {
                console.log("Dữ liệu từ API:", response.data);
                $scope.form = response.data;
                $scope.form.authorities = (response.data.authorities && Array.isArray(response.data.authorities))
                    ? response.data.authorities.map(auth => auth.authority)
                    : [];
                console.log("👀 Authorities hiện tại:", $scope.form.authorities);
                // if ($scope.form.image) {
                //     $scope.uploadedImage = `http://localhost:8080/uploads/images/${$scope.form.image}`;
                // } else {
                //     $scope.uploadedImage = null; // Nếu không có hình ảnh, xóa preview
                // }
                console.log(url);
            }).catch(error => {
                console.log(error);
            })
        }
        if ($scope.action === 'edit' || $routeParams.id) {
            $scope.title = "User Edition";
            console.log($scope.form);
            if ($routeParams.id) {
                console.log("Đang tải user với ID:", $routeParams.id);
                $scope.edit($routeParams.id); // Tải dữ liệu sản phẩm dựa trên ID
            } else {
                $scope.form = {enabled: true }; // Trường hợp tạo mới (không có ID)
            }
            $scope.loadAuthorities = function () {
                $http.get("http://localhost:8080/admin/authorities").then(response => {
                    if (Array.isArray(response.data)) {
                        let uniqueAuthorities = [...new Set(response.data.map(auth => auth.authority))];
                        $scope.authorities = uniqueAuthorities; // Gán vào biến, không phải hàm nữa
                        console.log("✅ Danh sách quyền:", uniqueAuthorities);
                    }
                }).catch(error => console.log(error));
            };

            // $scope.index_of = function (username, role){
            //     return $scope.authorities.findIndex(
            //         a => a.user.username === username && a.authority === role
            //     )
            // }
            // $scope.create = function () {
            //     var item = angular.copy($scope.form);
            //     item.id = null;
            //     $http.post(host, item).then(function (response) {
            //         $scope.items.push(response.data);
            //         $scope.reset();
            //         console.log(response);
            //     }).catch(function (error) {
            //         console.log(error);
            //     })
            // }
            $scope.update = function () {
                var item = angular.copy($scope.form);
                var url = `${host}/${$scope.form.username}`;
                delete item.authorities;
                $http.put(url, item).then(response => {
                    var index = $scope.items.findIndex(item => item.username === $scope.form.username);
                    if (index >= 0) {
                        $scope.items[index] = response.data;
                    }
                    $scope.updateAuthorities($scope.form.username, $scope.form.authorities || []);
                    console.log(response.data);
                    Swal.fire({
                        icon: 'success',
                        title: 'Cập nhật thành công!',
                        showConfirmButton: false,
                        timer: 1000
                    });
                }).catch(error => {
                    console.log(error);
                    Swal.fire({
                        icon: 'error',
                        title: 'Thêm mới thất bại!',
                        text: 'Đã có lỗi xảy ra. Vui lòng thử lại.'
                    });
                })
            }
            $scope.updateAuthorities = function (username, authorities) {
                var url = "http://localhost:8080/admin/authorities";

                $http.delete(`${url}/${username}`).then(() => {
                    console.log(`✅ Deleted old authorities for ${username}`);
                    if (!authorities || !Array.isArray(authorities)) {
                        console.log("No authorities to update or invalid format");
                        return;
                    }
                    authorities.forEach(role => {
                        var authority = { user: { username: username }, authority: role };
                        console.log(authority);
                        $http.post(url, authority).then(response => {
                            console.log(`✅ Added authority: ${role} for ${username}`);
                        }).catch(error => console.log("❌ Error adding authority:", error));
                    });

                }).catch(error => console.log("❌ Error deleting authorities:", error));
            };
            $scope.toggleAuthority = function (role) {
                if (!$scope.form.authorities) {
                    $scope.form.authorities = [];
                }
                let index = $scope.form.authorities.indexOf(role);
                if (index === -1) {
                    $scope.form.authorities.push(role); // Thêm quyền nếu chưa có
                } else {
                    $scope.form.authorities.splice(index, 1); // Xóa quyền nếu đã có
                }
            };
            $scope.reset = function () {
                $scope.form = {enabled: true };
                // $scope.uploadedImage = null;
            }
            // $scope.uploadFile = function (files) {
            //     if (files && files[0]) {
            //         var reader = new FileReader();
            //         reader.onload = function (e) {
            //             $scope.$apply(function () {
            //                 $scope.uploadedImage = e.target.result; // Gán data URL để hiển thị preview
            //             });
            //         };
            //         reader.readAsDataURL(files[0]);
            //         var data = new FormData();
            //         data.append("file", files[0]);
            //
            //         $http.post("http://localhost:8080/admin/upload/images", data, {
            //             transformRequest: angular.identity,
            //             headers: {'Content-Type': undefined}
            //         }).then(function (response) {
            //             // $scope.uploadedImage = "http://localhost:8080" + response.data.url;
            //             $scope.form.image = response.data.name;
            //
            //         }).catch(function (error) {
            //             console.log(error);
            //         });
            //     } else {
            //         alert("Vui lòng chọn file để tải lên.");
            //     }
            // }
            //
            $scope.loadAuthorities();
        } else if ($scope.action === 'list') {
            $scope.title = "User List";
            $scope.loadAll = function () {
                $http.get(host).then(response => {
                    $scope.items = response.data;
                    $scope.pager.setItems(PaginationService.sortBy($scope.items, $scope.sortColumn, $scope.reverse));
                    console.log(response.data);
                }).catch(error => {
                    console.log(error);
                });
            };

            $scope.pager = PaginationService.createPager(10);

            $scope.sortBy = function (column) {
                if ($scope.sortColumn === column) {
                    $scope.reverse = !$scope.reverse; // Đảo thứ tự nếu cùng cột
                } else {
                    $scope.sortColumn = column;
                    $scope.reverse = false; // Mặc định tăng dần khi đổi cột
                }
                $scope.pager.setItems(PaginationService.sortBy($scope.items, $scope.sortColumn, $scope.reverse));
            };
            $scope.sortByAuthority = function () {
                $scope.sortColumn = 'authority'; // Đặt tên cột để kiểm tra trạng thái
                if ($scope.sortColumn === 'authority' && $scope.reverse) {
                    $scope.reverse = false; // Đảo ngược nếu đã sắp xếp trước đó
                } else {
                    $scope.reverse = true;
                }

                // Sắp xếp items dựa trên authority đã loại bỏ "ROLE_"
                $scope.items.sort(function (a, b) {
                    var authA = a.authorities[0].authority.substring(5); // Lấy phần sau "ROLE_"
                    var authB = b.authorities[0].authority.substring(5);
                    if (authA < authB) return $scope.reverse ? 1 : -1;
                    if (authA > authB) return $scope.reverse ? -1 : 1;
                    return 0;
                });

                // Cập nhật lại pager nếu dùng phân trang
                if ($scope.pager) {
                    $scope.pager.setItems($scope.items);
                }
            };
            $scope.loadAll();
        }
        $rootScope.$on('searchResults/users/list', function(event, items) {
            $timeout(function() {
                $scope.items = items.length ? items : [];
                console.log("Dữ liệu sau khi cập nhật:", $scope.items);
                $scope.pager.setItems(PaginationService.sortBy($scope.items, $scope.sortColumn, $scope.reverse));
            });

        });

    });
})();
