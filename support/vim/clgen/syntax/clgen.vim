" Vim syntax file
" Language:         CLGen
" Maintainer:       Richard Senior
" URL:              https://github.com/sanhozay/CLGen
" Latest Revision:  2018-02-28

if exists("b:current_syntax")
    finish
endif

let s:cpo_save = &cpo
set cpo&vim

syn case match

" Comments
syn match   clgComment              '#.*$' contains=clgTodo
syn match   clgTodo                 contained 'TODO.*$'

" Keywords
syn keyword clgKeyword              project author item state marker if
syn keyword clgKeyword              checklist check text page
syn keyword clgBoolean              true false

" Strings
syn region  clgString               start='"' end='"' contains=clgEscaped
syn match   clgEscaped              contained '\\[\\\"]'

" Other tokens
syn match   clgIdentifier           '\I\i*'
syn match   clgNumber               '[+-]\?\d\+'
syn match   clgFloat                '[+-]\?\d\+\(\.\d*\)\?'


" fgcommand
syn cluster clgCommandComponents    contains=clgCommandName,clgCommandArgs
syn cluster clgCommandValues        contains=clgIdentifier,clgString,clgBoolean,clgNumber,clgFloat

syn keyword Keyword                 fgcommand nextgroup=clgCommandSpec skipwhite skipnl
syn region  clgCommandSpec          contained start='(' end=';' contains=@clgCommandComponents
syn region  clgCommandName          contained start='"' end='"' nextgroup=clgCommandArgs skipwhite skipnl
syn region  clgCommandArgs          contained start=',' end=')' contains=clgCommandKey,@clgCommandValues
syn region  clgCommandKey           contained start=','ms=s+1 end='='me=s-1

" Highlight links
hi def link clgComment              Comment
hi def link clgTodo                 Todo
hi def link clgKeyword              Keyword
hi def link clgBoolean              Boolean
hi def link clgString               String
hi def link clgEscaped              String
hi def link clgIdentifier           Identifier
hi def link clgNumber               Number
hi def link clgFloat                Float

hi def link clgCommandName          Function
hi def link clgCommandKey           Define

let b:current_syntax = 'clgen'

let &cpo = s:cpo_save
unlet s:cpo_save

