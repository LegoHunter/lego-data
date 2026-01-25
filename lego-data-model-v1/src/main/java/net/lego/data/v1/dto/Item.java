package net.lego.data.v1.dto;

import lombok.*;
import net.bricklink.data.lego.dto.BricklinkItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class Item {
    private int itemId;
    private String itemNumber;
    private String itemName;
    private Integer numberOfPieces;
    private Integer issueYear;
    private String issueLocation;
    private Integer themeId;
    private String itemTypeCode;
    private String notes;
    private List<Category> categories;
    private BricklinkItem brickLinkItem;
}
