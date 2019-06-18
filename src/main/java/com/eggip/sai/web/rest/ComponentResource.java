package com.eggip.sai.web.rest;


import com.eggip.sai.domain.Component;
import com.eggip.sai.repository.ComponentRepository;
import com.eggip.sai.service.dto.ComponentDTO;
import com.eggip.sai.service.mapper.ComponentMapper;
import com.eggip.sai.util.LRTree;
import com.eggip.sai.web.rest.errors.BadRequestAlertException;
import com.eggip.sai.web.rest.qo.ComponentQO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ComponentResource {
    private final Logger logger = LoggerFactory.getLogger(CategoryResource.class);


    @Autowired
    private LRTree lrTree;

    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private ComponentMapper componentMapper;


    @GetMapping("/components/root")
    public ResponseEntity<ComponentDTO> getRoot() throws URISyntaxException {
        ComponentDTO root = componentMapper.componentToComponentDTO(lrTree.getRoot(Component.class).orThrow().orElse(null));
        return ResponseEntity.created(new URI("/api/components/root"))
                .body(root);
    }


    @GetMapping("/components")
    public ResponseEntity<List<Component>> getComponets(ComponentQO componentQO) {
        return null;
    }


    /**
     * 获取指定组件的子组件
     * @return
     */
    @GetMapping("/component/{id}/children")
    public ResponseEntity<List<ComponentDTO>> getChildren(@PathVariable Integer id) throws URISyntaxException {
        if (id == null) {
            throw new BadRequestAlertException("An ID is required for requesting it's children", "componentManagement", "not found");
        }

        Optional<Component> component = componentRepository.findById(id);
        if (!component.isPresent()) {
            throw new BadRequestAlertException("Component doesn't exist", "componentManagement", "not found");
        }

        List<ComponentDTO> children =
                componentMapper.componentsToComponentDTOs(lrTree.getDirectChildren(Component.class, component.get()).orThrow().orElse(new ArrayList<>()));
        return ResponseEntity.created(new URI(String.format("/api/component/%s/children", id)))
                .body(children);
    }



    @PostMapping("/components")
    public ResponseEntity<ComponentDTO> createComponent(@Valid @RequestBody ComponentDTO componentDTO) throws URISyntaxException {
        if (componentDTO.getId() != null) {
            throw new BadRequestAlertException("A new component cannot already hava an ID", "componentManagement", "id exists");
        }

        if (componentRepository.findByAcReactiveKey(componentDTO.getAcReactiveKey()).isPresent()) {
            throw new BadRequestAlertException("Component already exists", "componentManagement", "exists");
        }

        Component component = componentMapper.componentDTOToComponent(componentDTO);
        if (component.getParentId() == null) {
            if (lrTree.getRoot(Component.class).orThrow().toOptional().isPresent()) {
                throw new BadRequestAlertException("A root component already exists", "componentManagement", "root exists");
            }

            lrTree.insert(Component.class, null, component);

        } else {
            Optional<Component> parent = componentRepository.findById(component.getParentId());
            if (!parent.isPresent()) {
                throw new BadRequestAlertException("Parent component doesn't exist", "componentManagement", "not found");
            }

            lrTree.insert(Component.class, parent.get(), component);
        }


        return ResponseEntity.created(new URI("/api/components"))
                .header("componentManagement.created", component.getAcReactiveKey())
                .body(componentMapper.componentToComponentDTO(component));

    }


    @PutMapping("/components")
    public ResponseEntity<Component> updateComponent(ComponentDTO componentDTO) {
        return null;
    }


    @GetMapping("/forbiddenTypes")
    public ResponseEntity<Component.ForbiddenType[]> getForbiddenTypes() throws URISyntaxException {
        return ResponseEntity.created(new URI("/api/forbiddenTypes"))
                .body(Component.ForbiddenType.values());
    }


    @GetMapping("/componentTypes")
    public ResponseEntity<Component.ComponentType[]> getComponentTypes() throws URISyntaxException {
        return ResponseEntity.created(new URI("/api/componentTypes"))
                .body(Component.ComponentType.values());
    }









}
