package web.mvc.entity.place;

import jakarta.persistence.*;
import lombok.*;
import web.mvc.entity.user.Users;

import java.util.List;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "place_seq")
    @SequenceGenerator(allocationSize = 1, sequenceName = "place_seq", name = "place_seq")
    private Long placeSeq;
    @Column(length = 100, unique = true)
    private String placeName;
    @Column(length = 100)
    private String placeAddress;
    @Column(length = 500)
    private String placeDescription;
    @Column(length = 300)
    private String placeMainImg;
    @Column(length = 100)
    private String placeMainImgType;
    @Column(length = 100)
    private String placeMainImgSize;

    @OneToMany(mappedBy = "placeRecommendation" , cascade = CascadeType.ALL)
    private List<PlaceDetailImg> placeDetailImgList;


}


