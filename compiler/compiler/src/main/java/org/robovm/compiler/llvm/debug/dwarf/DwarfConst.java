/*
 * Copyright 2016 Justin Shapcott.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.compiler.llvm.debug.dwarf;

/**
 * Dwarf constants as gleaned from the DWARF Debugging Information Format V.4
 * reference manual http://www.dwarfstd.org/.
 * translated from compiler/llvm/src/main/swig/include/llvm/Support/Dwarf.h
 */
@SuppressWarnings("unused")
public class DwarfConst {

    public interface DwarfConstEnum {
    }

    public static class LLVMConstants {
        // llvm mock tags
        public final static int TAG_invalid = ~0; // Tag for invalid results.

        // Other constants.
        public final static int DWARF_VERSION = 4;       // Default dwarf version we output.
        public final static int DEBUG_INFO_VERSION = 3;       // Default dwarf version we output.
        public final static int DW_PUBTYPES_VERSION = 2; // Section version number for .debug_pubtypes.
        public final static int DW_PUBNAMES_VERSION = 2; // Section version number for .debug_pubnames.
        public final static int DW_ARANGES_VERSION = 2;   // Section version number for .debug_aranges.
        // Identifiers we use to distinguish vendor extensions.
        public final static int DWARF_VENDOR_DWARF = 0; // Defined in v2 or later of the DWARF standard.
        public final static int DWARF_VENDOR_APPLE = 1;
        public final static int DWARF_VENDOR_BORLAND = 2;
        public final static int DWARF_VENDOR_GNU = 3;
        public final static int DWARF_VENDOR_GOOGLE = 4;
        public final static int DWARF_VENDOR_LLVM = 5;
        public final static int DWARF_VENDOR_MIPS = 6;
    }

    /// Constants that define the DWARF format as 32 or 64 bit.
    public enum DwarfFormat {
        DWARF32, DWARF64
    }

    public enum Tag {
        DW_TAG_null(0x0000),
        DW_TAG_array_type(0x0001),
        DW_TAG_class_type(0x0002),
        DW_TAG_entry_point(0x0003),
        DW_TAG_enumeration_type(0x0004),
        DW_TAG_formal_parameter(0x0005),
        DW_TAG_imported_declaration(0x0008),
        DW_TAG_label(0x000a),
        DW_TAG_lexical_block(0x000b),
        DW_TAG_member(0x000d),
        DW_TAG_pointer_type(0x000f),
        DW_TAG_reference_type(0x0010),
        DW_TAG_compile_unit(0x0011),
        DW_TAG_string_type(0x0012),
        DW_TAG_structure_type(0x0013),
        DW_TAG_subroutine_type(0x0015),
        DW_TAG_typedef(0x0016),
        DW_TAG_union_type(0x0017),
        DW_TAG_unspecified_parameters(0x0018),
        DW_TAG_variant(0x0019),
        DW_TAG_common_block(0x001a),
        DW_TAG_common_inclusion(0x001b),
        DW_TAG_inheritance(0x001c),
        DW_TAG_inlined_subroutine(0x001d),
        DW_TAG_module(0x001e),
        DW_TAG_ptr_to_member_type(0x001f),
        DW_TAG_set_type(0x0020),
        DW_TAG_subrange_type(0x0021),
        DW_TAG_with_stmt(0x0022),
        DW_TAG_access_declaration(0x0023),
        DW_TAG_base_type(0x0024),
        DW_TAG_catch_block(0x0025),
        DW_TAG_const_type(0x0026),
        DW_TAG_constant(0x0027),
        DW_TAG_enumerator(0x0028),
        DW_TAG_file_type(0x0029),
        DW_TAG_friend(0x002a),
        DW_TAG_namelist(0x002b),
        DW_TAG_namelist_item(0x002c),
        DW_TAG_packed_type(0x002d),
        DW_TAG_subprogram(0x002e),
        DW_TAG_template_type_parameter(0x002f),
        DW_TAG_template_value_parameter(0x0030),
        DW_TAG_thrown_type(0x0031),
        DW_TAG_try_block(0x0032),
        DW_TAG_variant_part(0x0033),
        DW_TAG_variable(0x0034),
        // New in DWARF v3:
        DW_TAG_volatile_type(0x0035),
        DW_TAG_dwarf_procedure(0x0036),
        DW_TAG_restrict_type(0x0037),
        DW_TAG_interface_type(0x0038),
        DW_TAG_namespace(0x0039),
        DW_TAG_imported_module(0x003a),
        DW_TAG_unspecified_type(0x003b),
        DW_TAG_partial_unit(0x003c),
        DW_TAG_imported_unit(0x003d),
        DW_TAG_condition(0x003f),
        DW_TAG_shared_type(0x0040),
        // New in DWARF v4:
        DW_TAG_type_unit(0x0041),
        DW_TAG_rvalue_reference_type(0x0042),
        DW_TAG_template_alias(0x0043),
        // New in DWARF v5:
        DW_TAG_coarray_type(0x0044),
        DW_TAG_generic_subrange(0x0045),
        DW_TAG_dynamic_type(0x0046),
        DW_TAG_atomic_type(0x0047),
        DW_TAG_call_site(0x0048),
        DW_TAG_call_site_parameter(0x0049),
        DW_TAG_skeleton_unit(0x004a),
        DW_TAG_immutable_type(0x004b),
        // Vendor extensions:
        DW_TAG_MIPS_loop(0x4081),
        DW_TAG_format_label(0x4101),
        DW_TAG_function_template(0x4102),
        DW_TAG_class_template(0x4103),
        DW_TAG_GNU_template_template_param(0x4106),
        DW_TAG_GNU_template_parameter_pack(0x4107),
        DW_TAG_GNU_formal_parameter_pack(0x4108),
        DW_TAG_GNU_call_site(0x4109),
        DW_TAG_GNU_call_site_parameter(0x410a),
        DW_TAG_APPLE_property(0x4200),
        DW_TAG_BORLAND_property(0xb000),
        DW_TAG_BORLAND_Delphi_string(0xb001),
        DW_TAG_BORLAND_Delphi_dynamic_array(0xb002),
        DW_TAG_BORLAND_Delphi_set(0xb003),
        DW_TAG_BORLAND_Delphi_variant(0xb004);

        public final int raw;

        Tag(int raw) {
            this.raw = raw;
        }
    }

    public enum Attribute {
        DW_AT_sibling(0x01),
        DW_AT_location(0x02),
        DW_AT_name(0x03),
        DW_AT_ordering(0x09),
        DW_AT_byte_size(0x0b),
        DW_AT_bit_offset(0x0c),
        DW_AT_bit_size(0x0d),
        DW_AT_stmt_list(0x10),
        DW_AT_low_pc(0x11),
        DW_AT_high_pc(0x12),
        DW_AT_language(0x13),
        DW_AT_discr(0x15),
        DW_AT_discr_value(0x16),
        DW_AT_visibility(0x17),
        DW_AT_import(0x18),
        DW_AT_string_length(0x19),
        DW_AT_common_reference(0x1a),
        DW_AT_comp_dir(0x1b),
        DW_AT_const_value(0x1c),
        DW_AT_containing_type(0x1d),
        DW_AT_default_value(0x1e),
        DW_AT_inline(0x20),
        DW_AT_is_optional(0x21),
        DW_AT_lower_bound(0x22),
        DW_AT_producer(0x25),
        DW_AT_prototyped(0x27),
        DW_AT_return_addr(0x2a),
        DW_AT_start_scope(0x2c),
        DW_AT_bit_stride(0x2e),
        DW_AT_upper_bound(0x2f),
        DW_AT_abstract_origin(0x31),
        DW_AT_accessibility(0x32),
        DW_AT_address_class(0x33),
        DW_AT_artificial(0x34),
        DW_AT_base_types(0x35),
        DW_AT_calling_convention(0x36),
        DW_AT_count(0x37),
        DW_AT_data_member_location(0x38),
        DW_AT_decl_column(0x39),
        DW_AT_decl_file(0x3a),
        DW_AT_decl_line(0x3b),
        DW_AT_declaration(0x3c),
        DW_AT_discr_list(0x3d),
        DW_AT_encoding(0x3e),
        DW_AT_external(0x3f),
        DW_AT_frame_base(0x40),
        DW_AT_friend(0x41),
        DW_AT_identifier_case(0x42),
        DW_AT_macro_info(0x43),
        DW_AT_namelist_item(0x44),
        DW_AT_priority(0x45),
        DW_AT_segment(0x46),
        DW_AT_specification(0x47),
        DW_AT_static_link(0x48),
        DW_AT_type(0x49),
        DW_AT_use_location(0x4a),
        DW_AT_variable_parameter(0x4b),
        DW_AT_virtuality(0x4c),
        DW_AT_vtable_elem_location(0x4d),

        // New in DWARF v3:
        DW_AT_allocated(0x4e),
        DW_AT_associated(0x4f),
        DW_AT_data_location(0x50),
        DW_AT_byte_stride(0x51),
        DW_AT_entry_pc(0x52),
        DW_AT_use_UTF8(0x53),
        DW_AT_extension(0x54),
        DW_AT_ranges(0x55),
        DW_AT_trampoline(0x56),
        DW_AT_call_column(0x57),
        DW_AT_call_file(0x58),
        DW_AT_call_line(0x59),
        DW_AT_description(0x5a),
        DW_AT_binary_scale(0x5b),
        DW_AT_decimal_scale(0x5c),
        DW_AT_small(0x5d),
        DW_AT_decimal_sign(0x5e),
        DW_AT_digit_count(0x5f),
        DW_AT_picture_string(0x60),
        DW_AT_mutable(0x61),
        DW_AT_threads_scaled(0x62),
        DW_AT_explicit(0x63),
        DW_AT_object_pointer(0x64),
        DW_AT_endianity(0x65),
        DW_AT_elemental(0x66),
        DW_AT_pure(0x67),
        DW_AT_recursive(0x68),

        // New in DWARF v4:
        DW_AT_signature(0x69),
        DW_AT_main_subprogram(0x6a),
        DW_AT_data_bit_offset(0x6b),
        DW_AT_const_expr(0x6c),
        DW_AT_enum_class(0x6d),
        DW_AT_linkage_name(0x6e),

        // New in DWARF v5:
        DW_AT_string_length_bit_size(0x6f),
        DW_AT_string_length_byte_size(0x70),
        DW_AT_rank(0x71),
        DW_AT_str_offsets_base(0x72),
        DW_AT_addr_base(0x73),
        DW_AT_rnglists_base(0x74),
        DW_AT_dwo_id(0x75),
        DW_AT_dwo_name(0x76),
        DW_AT_reference(0x77),
        DW_AT_rvalue_reference(0x78),
        DW_AT_macros(0x79),
        DW_AT_call_all_calls(0x7a),
        DW_AT_call_all_source_calls(0x7b),
        DW_AT_call_all_tail_calls(0x7c),
        DW_AT_call_return_pc(0x7d),
        DW_AT_call_value(0x7e),
        DW_AT_call_origin(0x7f),
        DW_AT_call_parameter(0x80),
        DW_AT_call_pc(0x81),
        DW_AT_call_tail_call(0x82),
        DW_AT_call_target(0x83),
        DW_AT_call_target_clobbered(0x84),
        DW_AT_call_data_location(0x85),
        DW_AT_call_data_value(0x86),
        DW_AT_noreturn(0x87),
        DW_AT_alignment(0x88),
        DW_AT_export_symbols(0x89),
        DW_AT_deleted(0x8a),
        DW_AT_defaulted(0x8b),
        DW_AT_loclists_base(0x8c),

        // Vendor extensions:
        DW_AT_MIPS_loop_begin(0x2002),
        DW_AT_MIPS_tail_loop_begin(0x2003),
        DW_AT_MIPS_epilog_begin(0x2004),
        DW_AT_MIPS_loop_unroll_factor(0x2005),
        DW_AT_MIPS_software_pipeline_depth(0x2006),
        DW_AT_MIPS_linkage_name(0x2007),
        DW_AT_MIPS_stride(0x2008),
        DW_AT_MIPS_abstract_name(0x2009),
        DW_AT_MIPS_clone_origin(0x200a),
        DW_AT_MIPS_has_inlines(0x200b),
        DW_AT_MIPS_stride_byte(0x200c),
        DW_AT_MIPS_stride_elem(0x200d),
        DW_AT_MIPS_ptr_dopetype(0x200e),
        DW_AT_MIPS_allocatable_dopetype(0x200f),
        DW_AT_MIPS_assumed_shape_dopetype(0x2010),

        // This one appears to have only been implemented by Open64 for
        // fortran and may conflict with other extensions.
        DW_AT_MIPS_assumed_size(0x2011),

        // GNU extensions
        DW_AT_sf_names(0x2101),
        DW_AT_src_info(0x2102),
        DW_AT_mac_info(0x2103),
        DW_AT_src_coords(0x2104),
        DW_AT_body_begin(0x2105),
        DW_AT_body_end(0x2106),
        DW_AT_GNU_vector(0x2107),
        DW_AT_GNU_template_name(0x2110),
        DW_AT_GNU_odr_signature(0x210f),
        DW_AT_GNU_call_site_value(0x2111),
        DW_AT_GNU_all_call_sites(0x2117),
        DW_AT_GNU_macros(0x2119),

        // Extensions for Fission proposal.
        DW_AT_GNU_dwo_name(0x2130),
        DW_AT_GNU_dwo_id(0x2131),
        DW_AT_GNU_ranges_base(0x2132),
        DW_AT_GNU_addr_base(0x2133),
        DW_AT_GNU_pubnames(0x2134),
        DW_AT_GNU_pubtypes(0x2135),
        DW_AT_GNU_discriminator(0x2136),

        // Borland extensions.
        DW_AT_BORLAND_property_read(0x3b11),
        DW_AT_BORLAND_property_write(0x3b12),
        DW_AT_BORLAND_property_implements(0x3b13),
        DW_AT_BORLAND_property_index(0x3b14),
        DW_AT_BORLAND_property_default(0x3b15),
        DW_AT_BORLAND_Delphi_unit(0x3b20),
        DW_AT_BORLAND_Delphi_class(0x3b21),
        DW_AT_BORLAND_Delphi_record(0x3b22),
        DW_AT_BORLAND_Delphi_metaclass(0x3b23),
        DW_AT_BORLAND_Delphi_constructor(0x3b24),
        DW_AT_BORLAND_Delphi_destructor(0x3b25),
        DW_AT_BORLAND_Delphi_anonymous_method(0x3b26),
        DW_AT_BORLAND_Delphi_interface(0x3b27),
        DW_AT_BORLAND_Delphi_ABI(0x3b28),
        DW_AT_BORLAND_Delphi_return(0x3b29),
        DW_AT_BORLAND_Delphi_frameptr(0x3b30),
        DW_AT_BORLAND_closure(0x3b31),

        // LLVM project extensions.
        DW_AT_LLVM_include_path(0x3e00),
        DW_AT_LLVM_config_macros(0x3e01),
        DW_AT_LLVM_isysroot(0x3e02),

        // Apple extensions.
        DW_AT_APPLE_optimized(0x3fe1),
        DW_AT_APPLE_flags(0x3fe2),
        DW_AT_APPLE_isa(0x3fe3),
        DW_AT_APPLE_block(0x3fe4),
        DW_AT_APPLE_major_runtime_vers(0x3fe5),
        DW_AT_APPLE_runtime_class(0x3fe6),
        DW_AT_APPLE_omit_frame_ptr(0x3fe7),
        DW_AT_APPLE_property_name(0x3fe8),
        DW_AT_APPLE_property_getter(0x3fe9),
        DW_AT_APPLE_property_setter(0x3fea),
        DW_AT_APPLE_property_attribute(0x3feb),
        DW_AT_APPLE_objc_complete_type(0x3fec),
        DW_AT_APPLE_property(0x3fed);

        public final int raw;

        Attribute(int raw) {
            this.raw = raw;
        }
    }

    // Attribute form encodings.
    public enum Form {
        DW_FORM_addr(0x01),
        DW_FORM_block2(0x03),
        DW_FORM_block4(0x04),
        DW_FORM_data2(0x05),
        DW_FORM_data4(0x06),
        DW_FORM_data8(0x07),
        DW_FORM_string(0x08),
        DW_FORM_block(0x09),
        DW_FORM_block1(0x0a),
        DW_FORM_data1(0x0b),
        DW_FORM_flag(0x0c),
        DW_FORM_sdata(0x0d),
        DW_FORM_strp(0x0e),
        DW_FORM_udata(0x0f),
        DW_FORM_ref_addr(0x10),
        DW_FORM_ref1(0x11),
        DW_FORM_ref2(0x12),
        DW_FORM_ref4(0x13),
        DW_FORM_ref8(0x14),
        DW_FORM_ref_udata(0x15),
        DW_FORM_indirect(0x16),

        // New in DWARF v4:
        DW_FORM_sec_offset(0x17),
        DW_FORM_exprloc(0x18),
        DW_FORM_flag_present(0x19),

        // This was defined out of sequence.
        DW_FORM_ref_sig8(0x20),

        // New in DWARF v5:
        DW_FORM_strx(0x1a),
        DW_FORM_addrx(0x1b),
        DW_FORM_ref_sup4(0x1c),
        DW_FORM_strp_sup(0x1d),
        DW_FORM_data16(0x1e),
        DW_FORM_line_strp(0x1f),
        DW_FORM_implicit_const(0x21),
        DW_FORM_loclistx(0x22),
        DW_FORM_rnglistx(0x23),
        DW_FORM_ref_sup8(0x24),
        DW_FORM_strx1(0x25),
        DW_FORM_strx2(0x26),
        DW_FORM_strx3(0x27),
        DW_FORM_strx4(0x28),
        DW_FORM_addrx1(0x29),
        DW_FORM_addrx2(0x2a),
        DW_FORM_addrx3(0x2b),
        DW_FORM_addrx4(0x2c),

        // Extensions for Fission proposal
        DW_FORM_GNU_addr_index(0x1f01),
        DW_FORM_GNU_str_index(0x1f02),

        // Alternate debug sections proposal (output of "dwz" tool).
        DW_FORM_GNU_ref_alt(0x1f20),
        DW_FORM_GNU_strp_alt(0x1f21);

        public final int raw;

        Form(int raw) {
            this.raw = raw;
        }
    }


    // DWARF Expression operators.
    public enum LocationAtom {
        DW_OP_addr(0x03),
        DW_OP_deref(0x06),
        DW_OP_const1u(0x08),
        DW_OP_const1s(0x09),
        DW_OP_const2u(0x0a),
        DW_OP_const2s(0x0b),
        DW_OP_const4u(0x0c),
        DW_OP_const4s(0x0d),
        DW_OP_const8u(0x0e),
        DW_OP_const8s(0x0f),
        DW_OP_constu(0x10),
        DW_OP_consts(0x11),
        DW_OP_dup(0x12),
        DW_OP_drop(0x13),
        DW_OP_over(0x14),
        DW_OP_pick(0x15),
        DW_OP_swap(0x16),
        DW_OP_rot(0x17),
        DW_OP_xderef(0x18),
        DW_OP_abs(0x19),
        DW_OP_and(0x1a),
        DW_OP_div(0x1b),
        DW_OP_minus(0x1c),
        DW_OP_mod(0x1d),
        DW_OP_mul(0x1e),
        DW_OP_neg(0x1f),
        DW_OP_not(0x20),
        DW_OP_or(0x21),
        DW_OP_plus(0x22),
        DW_OP_plus_uconst(0x23),
        DW_OP_shl(0x24),
        DW_OP_shr(0x25),
        DW_OP_shra(0x26),
        DW_OP_xor(0x27),
        DW_OP_bra(0x28),
        DW_OP_eq(0x29),
        DW_OP_ge(0x2a),
        DW_OP_gt(0x2b),
        DW_OP_le(0x2c),
        DW_OP_lt(0x2d),
        DW_OP_ne(0x2e),
        DW_OP_skip(0x2f),
        DW_OP_lit0(0x30),
        DW_OP_lit1(0x31),
        DW_OP_lit2(0x32),
        DW_OP_lit3(0x33),
        DW_OP_lit4(0x34),
        DW_OP_lit5(0x35),
        DW_OP_lit6(0x36),
        DW_OP_lit7(0x37),
        DW_OP_lit8(0x38),
        DW_OP_lit9(0x39),
        DW_OP_lit10(0x3a),
        DW_OP_lit11(0x3b),
        DW_OP_lit12(0x3c),
        DW_OP_lit13(0x3d),
        DW_OP_lit14(0x3e),
        DW_OP_lit15(0x3f),
        DW_OP_lit16(0x40),
        DW_OP_lit17(0x41),
        DW_OP_lit18(0x42),
        DW_OP_lit19(0x43),
        DW_OP_lit20(0x44),
        DW_OP_lit21(0x45),
        DW_OP_lit22(0x46),
        DW_OP_lit23(0x47),
        DW_OP_lit24(0x48),
        DW_OP_lit25(0x49),
        DW_OP_lit26(0x4a),
        DW_OP_lit27(0x4b),
        DW_OP_lit28(0x4c),
        DW_OP_lit29(0x4d),
        DW_OP_lit30(0x4e),
        DW_OP_lit31(0x4f),
        DW_OP_reg0(0x50),
        DW_OP_reg1(0x51),
        DW_OP_reg2(0x52),
        DW_OP_reg3(0x53),
        DW_OP_reg4(0x54),
        DW_OP_reg5(0x55),
        DW_OP_reg6(0x56),
        DW_OP_reg7(0x57),
        DW_OP_reg8(0x58),
        DW_OP_reg9(0x59),
        DW_OP_reg10(0x5a),
        DW_OP_reg11(0x5b),
        DW_OP_reg12(0x5c),
        DW_OP_reg13(0x5d),
        DW_OP_reg14(0x5e),
        DW_OP_reg15(0x5f),
        DW_OP_reg16(0x60),
        DW_OP_reg17(0x61),
        DW_OP_reg18(0x62),
        DW_OP_reg19(0x63),
        DW_OP_reg20(0x64),
        DW_OP_reg21(0x65),
        DW_OP_reg22(0x66),
        DW_OP_reg23(0x67),
        DW_OP_reg24(0x68),
        DW_OP_reg25(0x69),
        DW_OP_reg26(0x6a),
        DW_OP_reg27(0x6b),
        DW_OP_reg28(0x6c),
        DW_OP_reg29(0x6d),
        DW_OP_reg30(0x6e),
        DW_OP_reg31(0x6f),
        DW_OP_breg0(0x70),
        DW_OP_breg1(0x71),
        DW_OP_breg2(0x72),
        DW_OP_breg3(0x73),
        DW_OP_breg4(0x74),
        DW_OP_breg5(0x75),
        DW_OP_breg6(0x76),
        DW_OP_breg7(0x77),
        DW_OP_breg8(0x78),
        DW_OP_breg9(0x79),
        DW_OP_breg10(0x7a),
        DW_OP_breg11(0x7b),
        DW_OP_breg12(0x7c),
        DW_OP_breg13(0x7d),
        DW_OP_breg14(0x7e),
        DW_OP_breg15(0x7f),
        DW_OP_breg16(0x80),
        DW_OP_breg17(0x81),
        DW_OP_breg18(0x82),
        DW_OP_breg19(0x83),
        DW_OP_breg20(0x84),
        DW_OP_breg21(0x85),
        DW_OP_breg22(0x86),
        DW_OP_breg23(0x87),
        DW_OP_breg24(0x88),
        DW_OP_breg25(0x89),
        DW_OP_breg26(0x8a),
        DW_OP_breg27(0x8b),
        DW_OP_breg28(0x8c),
        DW_OP_breg29(0x8d),
        DW_OP_breg30(0x8e),
        DW_OP_breg31(0x8f),
        DW_OP_regx(0x90),
        DW_OP_fbreg(0x91),
        DW_OP_bregx(0x92),
        DW_OP_piece(0x93),
        DW_OP_deref_size(0x94),
        DW_OP_xderef_size(0x95),
        DW_OP_nop(0x96),
        // New in DWARF v3:
        DW_OP_push_object_address(0x97),
        DW_OP_call2(0x98),
        DW_OP_call4(0x99),
        DW_OP_call_ref(0x9a),
        DW_OP_form_tls_address(0x9b),
        DW_OP_call_frame_cfa(0x9c),
        DW_OP_bit_piece(0x9d),
        // New in DWARF v4:
        DW_OP_implicit_value(0x9e),
        DW_OP_stack_value(0x9f),
        // New in DWARF v5:
        DW_OP_implicit_pointer(0xa0),
        DW_OP_addrx(0xa1),
        DW_OP_constx(0xa2),
        DW_OP_entry_value(0xa3),
        DW_OP_const_type(0xa4),
        DW_OP_regval_type(0xa5),
        DW_OP_deref_type(0xa6),
        DW_OP_xderef_type(0xa7),
        DW_OP_convert(0xa8),
        DW_OP_reinterpret(0xa9),
        // Vendor extensions:
        // Extensions for GNU-style thread-local storage.
        DW_OP_GNU_push_tls_address(0xe0),
        // Extensions for Fission proposal.
        DW_OP_GNU_addr_index(0xfb),
        DW_OP_GNU_const_index(0xfc);

        public final int raw;

        LocationAtom(int raw) {
            this.raw = raw;
        }
    }

    // DWARF attribute type encodings.
    public enum TypeKind implements DwarfConstEnum{
        DW_ATE_address(0x01),
        DW_ATE_boolean(0x02),
        DW_ATE_complex_float(0x03),
        DW_ATE_float(0x04),
        DW_ATE_signed(0x05),
        DW_ATE_signed_char(0x06),
        DW_ATE_unsigned(0x07),
        DW_ATE_unsigned_char(0x08),
        // New in DWARF v3:
        DW_ATE_imaginary_float(0x09),
        DW_ATE_packed_decimal(0x0a),
        DW_ATE_numeric_string(0x0b),
        DW_ATE_edited(0x0c),
        DW_ATE_signed_fixed(0x0d),
        DW_ATE_unsigned_fixed(0x0e),
        DW_ATE_decimal_float(0x0f),
        // New in DWARF v4:
        DW_ATE_UTF(0x10),
        // New in DWARF v5:
        DW_ATE_UCS(0x11),
        DW_ATE_ASCII(0x12),
        DW_ATE_lo_user(0x80),
        DW_ATE_hi_user(0xff);

        public final int raw;

        TypeKind(int raw) {
            this.raw = raw;
        }
    }

    // Decimal sign attribute values
    public enum DecimalSignEncoding {
        DW_DS_unsigned(0x01),
        DW_DS_leading_overpunch(0x02),
        DW_DS_trailing_overpunch(0x03),
        DW_DS_leading_separate(0x04),
        DW_DS_trailing_separate(0x05);

        public final int raw;

        DecimalSignEncoding(int raw) {
            this.raw = raw;
        }
    }

    // Endianity attribute values
    public enum EndianityEncoding {
        DW_END_default(0x00),
        DW_END_big(0x01),
        DW_END_little(0x02),
        DW_END_lo_user(0x40),
        DW_END_hi_user(0xff);

        public final int raw;

        EndianityEncoding(int raw) {
            this.raw = raw;
        }
    }

    // Accessibility codes
    public enum AccessAttribute {
        DW_ACCESS_public(0x01),
        DW_ACCESS_protected(0x02),
        DW_ACCESS_private(0x03);

        public final int raw;

        AccessAttribute(int raw) {
            this.raw = raw;
        }
    }


    // Visibility codes
    public enum VisibilityAttribute {
        DW_VIS_local(0x01),
        DW_VIS_exported(0x02),
        DW_VIS_qualified(0x03);

        public final int raw;

        VisibilityAttribute(int raw) {
            this.raw = raw;
        }
    }

    // Virtuality codes
    public enum VirtualityAttribute {
        DW_VIRTUALITY_none(0x00),
        DW_VIRTUALITY_virtual(0x01),
        DW_VIRTUALITY_pure_virtual(0x02),
        DW_VIRTUALITY_max(0x02);

        public final int raw;

        VirtualityAttribute(int raw) {
            this.raw = raw;
        }
    }

    public enum DefaultedMemberAttribute {
        DW_DEFAULTED_no(0x00),
        DW_DEFAULTED_in_class(0x01),
        DW_DEFAULTED_out_of_class(0x02),
        DW_DEFAULTED_max(0x02);

        public final int raw;

        DefaultedMemberAttribute(int raw) {
            this.raw = raw;
        }
    }

    // DWARF languages.
    public enum SourceLanguage implements DwarfConstEnum {
        DW_LANG_C89(0x0001),
        DW_LANG_C(0x0002),
        DW_LANG_Ada83(0x0003),
        DW_LANG_C_plus_plus(0x0004),
        DW_LANG_Cobol74(0x0005),
        DW_LANG_Cobol85(0x0006),
        DW_LANG_Fortran77(0x0007),
        DW_LANG_Fortran90(0x0008),
        DW_LANG_Pascal83(0x0009),
        DW_LANG_Modula2(0x000a),
        // New in DWARF v3:
        DW_LANG_Java(0x000b),
        DW_LANG_C99(0x000c),
        DW_LANG_Ada95(0x000d),
        DW_LANG_Fortran95(0x000e),
        DW_LANG_PLI(0x000f),
        DW_LANG_ObjC(0x0010),
        DW_LANG_ObjC_plus_plus(0x0011),
        DW_LANG_UPC(0x0012),
        DW_LANG_D(0x0013),
        // New in DWARF v4:
        DW_LANG_Python(0x0014),
        // New in DWARF v5:
        DW_LANG_OpenCL(0x0015),
        DW_LANG_Go(0x0016),
        DW_LANG_Modula3(0x0017),
        DW_LANG_Haskell(0x0018),
        DW_LANG_C_plus_plus_03(0x0019),
        DW_LANG_C_plus_plus_11(0x001a),
        DW_LANG_OCaml(0x001b),
        DW_LANG_Rust(0x001c),
        DW_LANG_C11(0x001d),
        DW_LANG_Swift(0x001e),
        DW_LANG_Julia(0x001f),
        DW_LANG_Dylan(0x0020),
        DW_LANG_C_plus_plus_14(0x0021),
        DW_LANG_Fortran03(0x0022),
        DW_LANG_Fortran08(0x0023),
        DW_LANG_RenderScript(0x0024),
        DW_LANG_BLISS(0x0025),

        // Vendor extensions:
        DW_LANG_Mips_Assembler(0x8001),
        DW_LANG_GOOGLE_RenderScript(0x8e57),
        DW_LANG_BORLAND_Delphi(0xb000);

        public final int raw;

        SourceLanguage(int raw) {
            this.raw = raw;
        }
    }

    // Identifier case codes
    public enum CaseSensitivity {
        DW_ID_case_sensitive(0x00),
        DW_ID_up_case(0x01),
        DW_ID_down_case(0x02),
        DW_ID_case_insensitive(0x03);
        public final int raw;

        CaseSensitivity(int raw) {
            this.raw = raw;
        }
    }


    // DWARF calling convention codes.
    public enum CallingConvention {
        DW_CC_normal(0x01),
        DW_CC_program(0x02),
        DW_CC_nocall(0x03),
        // New in DWARF v5:
        DW_CC_pass_by_reference(0x04),
        DW_CC_pass_by_value(0x05),
        // Vendor extensions:
        DW_CC_GNU_renesas_sh(0x40),
        DW_CC_GNU_borland_fastcall_i386(0x41),
        DW_CC_BORLAND_safecall(0xb0),
        DW_CC_BORLAND_stdcall(0xb1),
        DW_CC_BORLAND_pascal(0xb2),
        DW_CC_BORLAND_msfastcall(0xb3),
        DW_CC_BORLAND_msreturn(0xb4),
        DW_CC_BORLAND_thiscall(0xb5),
        DW_CC_BORLAND_fastcall(0xb6),
        DW_CC_LLVM_vectorcall(0xc0),
        DW_CC_LLVM_Win64(0xc1),
        DW_CC_LLVM_X86_64SysV(0xc2),
        DW_CC_LLVM_AAPCS(0xc3),
        DW_CC_LLVM_AAPCS_VFP(0xc4),
        DW_CC_LLVM_IntelOclBicc(0xc5),
        DW_CC_LLVM_SpirFunction(0xc6),
        DW_CC_LLVM_OpenCLKernel(0xc7),
        DW_CC_LLVM_Swift(0xc8),
        DW_CC_LLVM_PreserveMost(0xc9),
        DW_CC_LLVM_PreserveAll(0xca),
        DW_CC_LLVM_X86RegCall(0xcb),
        // From GCC source code (include/dwarf2.h): This DW_CC_ value is not currently
        // generated by any toolchain.  It is used internally to GDB to indicate OpenCL C
        // functions that have been compiled with the IBM XL C for OpenCL compiler and use
        // a non-platform calling convention for passing OpenCL C vector types.
        DW_CC_GDB_IBM_OpenCL(0xff);

        public final int raw;

        CallingConvention(int raw) {
            this.raw = raw;
        }
    }

    // Inline codes
    public enum InlineAttribute {

        DW_INL_not_inlined(0x00),
        DW_INL_inlined(0x01),
        DW_INL_declared_not_inlined(0x02),
        DW_INL_declared_inlined(0x03);
        public final int raw;

        InlineAttribute(int raw) {
            this.raw = raw;
        }
    }


    public enum ArrayDimensionOrdering {
        // Array ordering
        DW_ORD_row_major(0x00),
        DW_ORD_col_major(0x01);
        public final int raw;

        ArrayDimensionOrdering(int raw) {
            this.raw = raw;
        }
    }

    public enum DiscriminantList {
        // Discriminant descriptor values
        DSC_label(0x00),
        DSC_range(0x01);
        public final int raw;

        DiscriminantList(int raw) {
            this.raw = raw;
        }
    }

    /// Line Number Standard Opcode Encodings.
    public enum LineNumberOps {
        DW_LNS_extended_op(0x00),
        DW_LNS_copy(0x01),
        DW_LNS_advance_pc(0x02),
        DW_LNS_advance_line(0x03),
        DW_LNS_set_file(0x04),
        DW_LNS_set_column(0x05),
        DW_LNS_negate_stmt(0x06),
        DW_LNS_set_basic_block(0x07),
        DW_LNS_const_add_pc(0x08),
        DW_LNS_fixed_advance_pc(0x09),
        // New in DWARF v3:
        DW_LNS_set_prologue_end(0x0a),
        DW_LNS_set_epilogue_begin(0x0b),
        DW_LNS_set_isa(0x0c);
        public final int raw;

        LineNumberOps(int raw) {
            this.raw = raw;
        }
    }


    // Line Number Extended Opcode Encodings
    public enum LineNumberExtendedOps {
        DW_LNE_end_sequence(0x01),
        DW_LNE_set_address(0x02),
        DW_LNE_define_file(0x03),
        // New in DWARF v4:
        DW_LNE_set_discriminator(0x04);
        public final int raw;

        LineNumberExtendedOps(int raw) {
            this.raw = raw;
        }
    }

    public enum MacinfoRecordType {
        // Macinfo Type Encodings
        DW_MACINFO_define(0x01),
        DW_MACINFO_undef(0x02),
        DW_MACINFO_start_file(0x03),
        DW_MACINFO_end_file(0x04),
        DW_MACINFO_vendor_ext(0xff);
        public final int raw;

        MacinfoRecordType(int raw) {
            this.raw = raw;
        }
    }


    /// DWARF v5 macro information entry type encodings.
    public enum MacroEntryType {
        // DWARF v5 Macro information.
        DW_MACRO_define(0x01),
        DW_MACRO_undef(0x02),
        DW_MACRO_start_file(0x03),
        DW_MACRO_end_file(0x04),
        DW_MACRO_define_strp(0x05),
        DW_MACRO_undef_strp(0x06),
        DW_MACRO_import(0x07),
        DW_MACRO_define_sup(0x08),
        DW_MACRO_undef_sup(0x09),
        DW_MACRO_import_sup(0x0a),
        DW_MACRO_define_strx(0x0b),
        DW_MACRO_undef_strx(0x0c);
        public final int raw;

        MacroEntryType(int raw) {
            this.raw = raw;
        }
    }

    /// DWARF v5 range list entry encoding values.
    public enum RangeListEntries {
        // DWARF v5 Range List Entry encoding values.

        DW_RLE_end_of_list(0x00),
        DW_RLE_base_addressx(0x01),
        DW_RLE_startx_endx(0x02),
        DW_RLE_startx_length(0x03),
        DW_RLE_offset_pair(0x04),
        DW_RLE_base_address(0x05),
        DW_RLE_start_end(0x06),
        DW_RLE_start_length(0x07);
        public final int raw;

        RangeListEntries(int raw) {
            this.raw = raw;
        }
    }

    /// Call frame instruction encodings.
    public enum CallFrameInfo {
        // Call frame instruction encodings.
        DW_CFA_nop(0x00),
        DW_CFA_advance_loc(0x40),
        DW_CFA_offset(0x80),
        DW_CFA_restore(0xc0),
        DW_CFA_set_loc(0x01),
        DW_CFA_advance_loc1(0x02),
        DW_CFA_advance_loc2(0x03),
        DW_CFA_advance_loc4(0x04),
        DW_CFA_offset_extended(0x05),
        DW_CFA_restore_extended(0x06),
        DW_CFA_undefined(0x07),
        DW_CFA_same_value(0x08),
        DW_CFA_register(0x09),
        DW_CFA_remember_state(0x0a),
        DW_CFA_restore_state(0x0b),
        DW_CFA_def_cfa(0x0c),
        DW_CFA_def_cfa_register(0x0d),
        DW_CFA_def_cfa_offset(0x0e),
        // New in DWARF v3:
        DW_CFA_def_cfa_expression(0x0f),
        DW_CFA_expression(0x10),
        DW_CFA_offset_extended_sf(0x11),
        DW_CFA_def_cfa_sf(0x12),
        DW_CFA_def_cfa_offset_sf(0x13),
        DW_CFA_val_offset(0x14),
        DW_CFA_val_offset_sf(0x15),
        DW_CFA_val_expression(0x16),
        // Vendor extensions:
        DW_CFA_MIPS_advance_loc8(0x1d),
        DW_CFA_GNU_window_save(0x2d),
        DW_CFA_GNU_args_size(0x2e);
        public final int raw;

        CallFrameInfo(int raw) {
            this.raw = raw;
        }
    }

    public enum Constants {
        // Children flag
        DW_CHILDREN_no(0x00),
        DW_CHILDREN_yes(0x01),

        DW_EH_PE_absptr(0x00),
        DW_EH_PE_omit(0xff),
        DW_EH_PE_uleb128(0x01),
        DW_EH_PE_udata2(0x02),
        DW_EH_PE_udata4(0x03),
        DW_EH_PE_udata8(0x04),
        DW_EH_PE_sleb128(0x09),
        DW_EH_PE_sdata2(0x0A),
        DW_EH_PE_sdata4(0x0B),
        DW_EH_PE_sdata8(0x0C),
        DW_EH_PE_signed(0x08),
        DW_EH_PE_pcrel(0x10),
        DW_EH_PE_textrel(0x20),
        DW_EH_PE_datarel(0x30),
        DW_EH_PE_funcrel(0x40),
        DW_EH_PE_aligned(0x50),
        DW_EH_PE_indirect(0x80);
        public final int raw;

        Constants(int raw) {
            this.raw = raw;
        }
    }

    /// Constants for location lists in DWARF v5.
    public enum LocationListEntry {
        DW_LLE_end_of_list(0x00),
        DW_LLE_base_addressx(0x01),
        DW_LLE_startx_endx(0x02),
        DW_LLE_startx_length(0x03),
        DW_LLE_offset_pair(0x04),
        DW_LLE_default_location(0x05),
        DW_LLE_base_address(0x06),
        DW_LLE_start_end(0x07),
        DW_LLE_start_length(0x08);
        public final int raw;

        LocationListEntry(int raw) {
            this.raw = raw;
        }
    }


    /// Constants for the DW_APPLE_PROPERTY_attributes attribute.
    /// Keep this list in sync with clang's DeclSpec.h ObjCPropertyAttributeKind!
    public enum ApplePropertyAttributes {
        DW_APPLE_PROPERTY_readonly(0x01),
        DW_APPLE_PROPERTY_getter(0x02),
        DW_APPLE_PROPERTY_assign(0x04),
        DW_APPLE_PROPERTY_readwrite(0x08),
        DW_APPLE_PROPERTY_retain(0x10),
        DW_APPLE_PROPERTY_copy(0x20),
        DW_APPLE_PROPERTY_nonatomic(0x40),
        DW_APPLE_PROPERTY_setter(0x80),
        DW_APPLE_PROPERTY_atomic(0x100),
        DW_APPLE_PROPERTY_weak(0x200),
        DW_APPLE_PROPERTY_strong(0x400),
        DW_APPLE_PROPERTY_unsafe_unretained(0x800),
        DW_APPLE_PROPERTY_nullability(0x1000),
        DW_APPLE_PROPERTY_null_resettable(0x2000),
        DW_APPLE_PROPERTY_class(0x4000);
        public final int raw;

        ApplePropertyAttributes(int raw) {
            this.raw = raw;
        }
    }

    /// Constants for unit types in DWARF v5.
    public enum UnitType {

        DW_UT_compile(0x01),
        DW_UT_type(0x02),
        DW_UT_partial(0x03),
        DW_UT_skeleton(0x04),
        DW_UT_split_compile(0x05),
        DW_UT_split_type(0x06);
        public final int raw;

        UnitType(int raw) {
            this.raw = raw;
        }
    }

    public enum Index {
        DW_IDX_compile_unit(0x01),
        DW_IDX_type_unit(0x02),
        DW_IDX_die_offset(0x03),
        DW_IDX_parent(0x04),
        DW_IDX_type_hash(0x05);
        public final int raw;

        Index(int raw) {
            this.raw = raw;
        }
    }

    // Constants for the DWARF v5 Accelerator Table Proposal
    public enum AcceleratorTable {
        // Data layout descriptors.
        DW_ATOM_null(0),         ///  Marker as the end of a list of atoms.
        DW_ATOM_die_offset(1),   // DIE offset in the debug_info section.
        DW_ATOM_cu_offset(2),    // Offset of the compile unit header that contains the item in question.
        DW_ATOM_die_tag(3),      // A tag entry.
        DW_ATOM_type_flags(4),   // Set of flags for a type.

        DW_ATOM_type_type_flags(5), // Dsymutil type extension.
        DW_ATOM_qual_name_hash(6),  // Dsymutil qualified hash extension.

        // DW_ATOM_type_flags values.

        // Always set for C++, only set for ObjC if this is the @implementation for a
        // class.
        DW_FLAG_type_implementation(2),

        // Hash functions.

        // Daniel J. Bernstein hash.
        DW_hash_function_djb(0);
        public final int raw;

        AcceleratorTable(int raw) {
            this.raw = raw;
        }
    }

    /// This enumeration defines the supported behaviors of module flags.
    public enum ModuleFlagBehavior implements DwarfConstEnum{
        /// Emits an error if two values disagree, otherwise the resulting value is
        /// that of the operands.
        Error(1),

        /// Emits a warning if two values disagree. The result value will be the
        /// operand for the flag from the first module being linked.
        Warning(2),

        /// Adds a requirement that another module flag be present and have a
        /// specified value after linking is performed. The value must be a metadata
        /// pair, where the first element of the pair is the ID of the module flag
        /// to be restricted, and the second element of the pair is the value the
        /// module flag should be restricted to. This behavior can be used to
        /// restrict the allowable results (via triggering of an error) of linking
        /// IDs with the **Override** behavior.
        Require(3),

        /// Uses the specified value, regardless of the behavior or value of the
        /// other module. If both modules specify **Override**, but the values
        /// differ, an error will be emitted.
        Override(4),

        /// Appends the two values, which are required to be metadata nodes.
        Append(5),

        /// Appends the two values, which are required to be metadata
        /// nodes. However, duplicate entries in the second list are dropped
        /// during the append operation.
        AppendUnique(6),

        /// Takes the max of the two values, which are required to be integers.
        Max(7);

        public final int raw;

        ModuleFlagBehavior(int raw) {
            this.raw = raw;
        }
    }
}
