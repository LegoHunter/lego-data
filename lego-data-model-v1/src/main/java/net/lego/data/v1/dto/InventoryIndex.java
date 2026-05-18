package net.lego.data.v1.dto;

import lombok.*;

@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class InventoryIndex {
    private Integer boxId;
    private Integer boxIndex;
    private String itemNumber;
    private String boxName;
    private String boxNumber;
    private String sealed;
    private Integer quantity;
    private String description;
    private boolean active;
    private Integer movedToBoxId;
}
