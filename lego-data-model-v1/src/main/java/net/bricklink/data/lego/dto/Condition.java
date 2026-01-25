package net.bricklink.data.lego.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class Condition {
    private int conditionId;
    private String conditionCode;
    private String conditionDescription;
    private String conditionText;
}
