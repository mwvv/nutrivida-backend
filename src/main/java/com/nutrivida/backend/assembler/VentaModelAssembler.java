package com.nutrivida.backend.assembler;

import com.nutrivida.backend.controller.VentaController;
import com.nutrivida.backend.model.Venta;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class VentaModelAssembler implements RepresentationModelAssembler<Venta, EntityModel<Venta>> {

    @Override
    public EntityModel<Venta> toModel(Venta venta) {
        return EntityModel.of(venta,
                linkTo(methodOn(VentaController.class).buscar(venta.getId())).withSelfRel(),
                linkTo(methodOn(VentaController.class).listar()).withRel("todos"));
    }

    @Override
    public CollectionModel<EntityModel<Venta>> toCollectionModel(Iterable<? extends Venta> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities)
                .add(linkTo(methodOn(VentaController.class).listar()).withSelfRel());
    }
}
