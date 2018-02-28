" Vim syntax file
" Language:         CLGen
" Maintainer:       Richard Senior
" URL:              https://github.com/sanhozay/CLGen
" Latest Revision:  2018-02-28

if !exists("main_syntax")
    if version < 600
        syntax clear
    elseif exists("b:current_syntax")
        finish
    endif
    let main_syntax = 'clgen'
endif

syntax match   Comment                '#.*$' contains=Todo
syntax match   Todo                   'TODO.*$' contained

syntax keyword Keyword                project author item state marker if
syntax keyword Keyword                checklist check text page
syntax keyword Boolean                true false

syntax region  String                 start='"' end='"' contains=clgEscaped
syntax match   clgEscaped             contained '\\[\\\"]'

syntax match   Identifier             '\I\i*'
syntax match   Number                 '[+-]\?\d\+'
syntax match   Float                  '[+-]\?\d\+\(\.\d*\)\?'

syntax region  clgCommandRegion       start='fgcommand' end=';' contains=ALLBUT,Comment,Todo
syntax keyword clgCommand             contained fgcommand
syntax region  Define                 contained start=',\s*' end='='me=s-1

highlight default link clgCommand     Keyword
highlight default link clgEscaped     String
