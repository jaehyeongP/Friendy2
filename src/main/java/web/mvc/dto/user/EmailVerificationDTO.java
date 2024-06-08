package web.mvc.dto.user;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationDTO {

    private Long emailVerificationSeq;
    private String emailToken;
}
