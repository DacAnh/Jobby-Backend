package vn.hoidanit.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.hoidanit.jobhunter.domain.User;

// Thêm JpaSpecificationExecutor để kích hoạt filter
@Repository
public interface UserRepository extends JpaRepository<User,Long>, JpaSpecificationExecutor<User> {
    User findOneByEmail(String email);
    boolean existsByEmail(String email);
    User findByEmailAndRefreshToken(String email, String refreshToken);
}
