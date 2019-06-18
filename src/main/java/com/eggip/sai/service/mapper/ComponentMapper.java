package com.eggip.sai.service.mapper;

import com.eggip.sai.domain.Component;
import com.eggip.sai.service.dto.ComponentDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComponentMapper {


    public List<ComponentDTO> componentsToComponentDTOs(List<Component> components) {
        if (components == null) return null;
        return components.stream().map(c -> componentToComponentDTO(c)).collect(Collectors.toList());
    }


    public Component componentDTOToComponent(ComponentDTO componentDTO) {
        if (componentDTO == null) return null;
        return Component.builder()
                .acReactiveKey(componentDTO.getAcReactiveKey())
                .api(componentDTO.getApi())
                .componentType(componentDTO.getComponentType())
                .forbiddenType(componentDTO.getForbiddenType())
                .id(componentDTO.getId())
                .name(componentDTO.getName())
                .parentId(componentDTO.getParentId())
                .route(componentDTO.getRoute())
                .build();
    }

    public ComponentDTO componentToComponentDTO(Component component) {
        if (component == null) return null;
        return ComponentDTO.builder()
                .acReactiveKey(component.getAcReactiveKey())
                .api(component.getApi())
                .componentType(component.getComponentType())
                .forbiddenType(component.getForbiddenType())
                .id(component.getId())
                .name(component.getName())
                .parentId(component.getParentId())
                .route(component.getRoute())
                .build();

    }





}
