package com.assignment.service.impl;

import com.assignment.dao.AuthorityDAO;
import com.assignment.dao.UserDAO;
import com.assignment.dao.UserProfileDAO;
import com.assignment.entity.*;
import com.assignment.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private AuthorityDAO authorityDAO;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> loadAll() {
        return userDAO.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return userDAO.findById(username).orElse(null);
    }

//    @Override
//    public User save(User user) {
//        User existingUser = userDAO.findById(user.getUsername()).orElse(null);
//        System.out.println("pass của user: " + user.getPassword());
//        if (existingUser != null) {
//            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
//                System.out.println("pass của existingUser: " + existingUser.getPassword());
//                // Kiểm tra xem mật khẩu gửi lên đã được mã hóa chưa
//                if (!user.getPassword().startsWith("$2a$") && !user.getPassword().startsWith("$2b$") && !user.getPassword().startsWith("$2y$")) {
//                    // Nếu là plain text, mã hóa nó
//                    user.setPassword(passwordEncoder.encode(user.getPassword()));
//                    System.out.println("Đã mã hóa pass: " + user.getPassword());
//                } else if (!user.getPassword().equals(existingUser.getPassword())) {
//                    // Nếu đã mã hóa nhưng khác với mật khẩu cũ, cập nhật nó
//                    user.setPassword(passwordEncoder.encode(user.getPassword()));
//                    System.out.println("Đã mã hóa pass: " + user.getPassword());
//                }
//            } else {
//                user.setPassword(existingUser.getPassword());
//            }
//        } else {
//            // Nếu là user mới, mã hóa mật khẩu nếu chưa mã hóa
//            if (user.getPassword() != null && !user.getPassword().isEmpty() &&
//                    !user.getPassword().startsWith("$2a$") && !user.getPassword().startsWith("$2b$") && !user.getPassword().startsWith("$2y$")) {
//                user.setPassword(passwordEncoder.encode(user.getPassword()));
//            }
//        }
//        return userDAO.save(user);
//    }
//@Override
//public User save(User user) {
//    boolean isNewUser = userDAO.findById(user.getUsername()).isEmpty();
//
//    if (isNewUser) {
//        return createUser(user);
//    } else {
//        return updateUser(user);
//    }
//}
    @Override
    @Transactional
    public User createUser(User user) {
        // Mã hóa mật khẩu nếu cần
        System.out.println("1");
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(""));
        } else if (!user.getPassword().startsWith("$2a$") &&
                !user.getPassword().startsWith("$2b$") &&
                !user.getPassword().startsWith("$2y$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        user.setEnabled(true);

//        if (user.getUserProfile() != null) {
//            user.getUserProfile().setUser(user); // Thiết lập quan hệ 2 chiều
//        }
        System.out.println("service"+user);
//        User savedUser =
        entityManager.persist(user);
        System.out.println("2");
        Authority authority = new Authority();
        authority.setUser(user);
        authority.setAuthority("ROLE_GUEST");
        authorityDAO.save(authority);
        System.out.println("3");
        return user;
    }
    @Override
    public User updateUser(User user) {
        User existingUser = userDAO.findById(user.getUsername()).orElseThrow();

        // Cập nhật mật khẩu nếu có
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            if (!user.getPassword().startsWith("$2a$") &&
                    !user.getPassword().startsWith("$2b$") &&
                    !user.getPassword().startsWith("$2y$")) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                existingUser.setPassword(user.getPassword());
            }
        }
        existingUser.setEnabled(user.getEnabled());
        // Cập nhật thông tin profile nếu có
        if (user.getUserProfile() != null) {
            UserProfile profile = existingUser.getUserProfile();
            profile.setFullname(user.getUserProfile().getFullname());
            profile.setEmail(user.getUserProfile().getEmail());
            profile.setPhoto(user.getUserProfile().getPhoto());
        }

        authorityDAO.deleteByUser(existingUser); // Xóa quyền cũ
        List<Authority> newAuthorities = new ArrayList<>();
        if (user.getAuthorities() != null) {
            for (Authority auth : user.getAuthorities()) {
                Authority authority = new Authority();
                authority.setUser(existingUser);
                authority.setAuthority(auth.getAuthority());
                newAuthorities.add(authority);
            }
        } else {
            // Xử lý nếu authorities là null, hoặc khởi tạo danh sách mới
            user.setAuthorities(new ArrayList<>());
        }

        authorityDAO.saveAll(newAuthorities); // Lưu quyền mới

        return userDAO.save(existingUser);
    }

    @Override
    public Boolean existsByUsername(String username) {
        return userDAO.existsById(username);
    }

    @Override
    public void delete(String username) {
        userDAO.deleteById(username);
    }

    @Override
    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return userDAO.findAll();
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.like(cb.lower(root.get("username")), "%" + keyword.toLowerCase() + "%"));

        Join<User, UserProfile> userUserProfileJoin = root.join("userProfile", JoinType.LEFT);
        predicates.add(cb.like(cb.lower(userUserProfileJoin.get("fullname")), "%" + keyword.toLowerCase() + "%"));
        predicates.add(cb.like(cb.lower(userUserProfileJoin.get("email")), "%" + keyword.toLowerCase() + "%"));

        Join<User, Authority> authorityJoin = root.join("authorities", JoinType.LEFT);
        predicates.add(cb.like(cb.lower(authorityJoin.get("authority")), "%" + keyword.toLowerCase() + "%"));

        cq.where(cb.or(predicates.toArray(new Predicate[0])));

        // Thực thi truy vấn
        return entityManager.createQuery(cq).getResultList();
    }

//    @Override
//    public User updateUser(User user) {
//        User existingUser = userDAO.findById(user.getUsername()).orElse(null);
//        if (existingUser == null) return null;
//
//        existingUser.setPassword(user.getPassword());
//        existingUser.setEnabled(user.getEnabled());
//
//        // Cập nhật authorities
//        authorityDAO.deleteByUser(existingUser); // Xóa quyền cũ
//        List<Authority> newAuthorities = new ArrayList<>();
//        if (user.getAuthorities() != null) {
//            for (Authority auth : user.getAuthorities()) {
//                Authority authority = new Authority();
//                authority.setUser(existingUser);
//                authority.setAuthority(auth.getAuthority());
//                newAuthorities.add(authority);
//            }
//        } else {
//            // Xử lý nếu authorities là null, hoặc khởi tạo danh sách mới
//            user.setAuthorities(new ArrayList<>());
//        }
//
//        authorityDAO.saveAll(newAuthorities); // Lưu quyền mới
//
//        return userDAO.save(existingUser);
//    }
}
