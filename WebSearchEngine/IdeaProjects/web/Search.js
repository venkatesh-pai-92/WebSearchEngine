/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
$(document).ready(function () {
    $('#parameters_container').hide();
    $('#set_parameters').click(function () {
        if ($(this).val() == "Set Parameters") {
            $('#parameters_container').show();
            $(this).val("Hide Parameters");
        } else if ($(this).val() == "Hide Parameters") {
            $('#parameters_container').hide();
            $(this).val("Set Parameters");
        }
    });
    $('#hiddenLanguage').click(function () {
        if ($(this).val() == "Try Deutsch") {
            $(this).val("Versuchen Sie Englisch");
            $('#language').val("de");
        } else if ($(this).val() == "Versuchen Sie Englisch") {
            $(this).val("Try Deutsch");
            $('#language').val("en");
        }
    });
    $('#search-json').hide();
    $('#show_json').click(function () {
        if ($(this).val() == "Show JSON") {
            $(this).val("Show Results");
            $('#search-json').show();
            $('#search-results').hide();
        } else if ($(this).val() == "Show Results") {
            $(this).val("Show JSON");
            $('#search-results').show();
            $('#search-json').hide();
        }
    });
    $('#doyoumean').click(function () {
        var query = $(this).text().slice(13);
        $('#query').val(query);
        $(this).hide();
    });

    $('#images-text').click(function () {
        if ($(this).text().trim() == "Images") {
            $(this).text("All");
            $('#imageSearch').val("true");
        } else if ($(this).text().trim() == "All") {
            $(this).text("Images");
            $('#imageSearch').val("false");
        }
    });
    $('#all-tab').click(function () {
        $('#imageSearch').val("false");
        $("form").submit();
    });

    $('#image-tab').click(function () {
        $('#imageSearch').val("true");
        $("form").submit();
    });
    //Configure search engine
    $('#add-search-engine').hide();
    $('#btn-add-search-engine').click(function () {
        $("#group-id").prop('disabled', false);
        $("#group-id").val("");
        $("#config-url").val("");
        $('#btn-add').text("Add");
        $("#is-active").prop('checked', false);
        if ($(this).text() == "Add Search Engine") {
            $(this).text("Close");
            $('#add-search-engine').show(300);
        } else if ($(this).text() == "Close") {
            $(this).text("Add Search Engine");
            $('#add-search-engine').hide(300);
        }
    });

    //element to delete:
    var ele_del;

// Expose popup message when del button clicked:
    $('.btn-delete-row').click(function () {
        event.preventDefault();
        $('#popup').fadeIn('slow');
        ele_del = $(this).closest('tr');
        return false;
    });

//delete if proceed clicked:
    $('button#confirm').click(function () {
        event.preventDefault();
        $('#popup').fadeOut('slow');
        var deleteId = $(ele_del).find('.td-group-id').text().trim();
        $('#delete-id').val(deleteId);
        $('#action').val("Delete");
        $('#form-searchEngine').submit();
    });

//cancel delete operation:
    $('button#cancel').click(function () {
        event.preventDefault();
        $('#popup').fadeOut('slow', function () {
            ele_del = "";
        });
        return false;
    });

    $('.btn-edit-row').click(function () {

    });
// Register ad Validation
    $('#reg-ad').bootstrapValidator({
        // To use feedback icons, ensure that you use Bootstrap v3.1.0 or later
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            user_name: {
                validators: {
                    stringLength: {
                        min: 2,
                    },
                    notEmpty: {
                        message: 'Please supply your user name'
                    }
                }
            },
            n_grams: {
                validators: {
                    stringLength: {
                        min: 2,
                    },
                    notEmpty: {
                        message: 'Please enter n-grams'
                    }
                }
            },

            url: {
                validators: {
                    notEmpty: {
                        message: 'Please provide url'
                    },
                    uri: {
                        message: 'Please provide a valid url'
                    }
                }
            },
            description: {
                validators: {
                    stringLength: {
                        min: 10,
                    },
                    notEmpty: {
                        message: 'Please describe at least 10 characters'
                    }
                }
            },
            budget: {
                validators: {
                    notEmpty: {
                        message: 'Please provide a budget'
                    },
                    numeric: {
                        message: 'The value is not a number',
                        decimalSeparator: '.'
                    }
                }
            },
            money_per_click: {
                validators: {
                    notEmpty: {
                        message: 'Please provide a valid amount'
                    },
                    numeric: {
                        message: 'The value is not a number',
                        decimalSeparator: '.'
                    }
                }
            },
            image_url: {
                validators: {
                    uri: {
                        message: 'Please provide a valid url'
                    }
                }
            }
        }
    }).on('success.form.bv', function (e) {
        $('#success_message').slideDown({opacity: "show"}, "slow") // Do something ...
        $('#reg-ad').data('bootstrapValidator').resetForm();

        // Prevent form submission
        e.preventDefault();

        // Get the form instance
        var $form = $(e.target);

        // Get the BootstrapValidator instance
        var bv = $form.data('bootstrapValidator');

        // Use Ajax to submit form data
        $.post($form.attr('action'), $form.serialize(), function (result) {
            console.log(result);
        }, 'json');
    });

    $('.btn-edit-row').click(function () {
        $('#btn-add').text("Update");
        var groupId = $(this).parent().parent().find('.td-group-id').text().trim();
        $('#group-id').val(groupId);
        $("#group-id").prop('disabled', true);
        var configUrl = $(this).parent().parent().find('.td-config-url').text().trim();
        $('#config-url').val(configUrl);
        var active = $(this).parent().parent().find('.td-active').text();
        if (active == "Active") {
            $('#is-active').prop("checked", true);
        }
        $('#add-search-engine').show(300);
        $('#btn-add-search-engine').text("Close");
    });

    $('#btn-add').click(function () {
        if ($(this) .text() == "Add") {
            $('#action').val("Add");
        }else if ($(this) .text() == "Update") {
            $('#action').val("Update");
            $('#group-id').prop('disabled', false);
        }
        $('#form-searchEngine').submit();
    });

    $('#is-active').change(function(){
        if($(this).prop('checked')){
            $(this).val('true');
        }else{
            $(this).val('false');
        }
    });
});

