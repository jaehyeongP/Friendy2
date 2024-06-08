package web.mvc.repository.user;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import web.mvc.domain.user.Users;

public interface UserRepository extends JpaRepository<Users, Long> {

    @Query("select u from Users u where u.userId = ?1")
    public Users findUserByUserId(String userId);
}
